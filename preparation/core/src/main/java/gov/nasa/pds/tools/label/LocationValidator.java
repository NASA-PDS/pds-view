package gov.nasa.pds.tools.label;

import gov.nasa.pds.tools.label.validate.DocumentValidator;
import gov.nasa.pds.tools.util.SettingsManager;
import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.TargetRegistrar;
import gov.nasa.pds.tools.validate.ValidationProblem;
import gov.nasa.pds.tools.validate.rule.RuleContext;
import gov.nasa.pds.tools.validate.rule.ValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationRuleManager;
import gov.nasa.pds.tools.validate.task.BlockingTaskManager;
import gov.nasa.pds.tools.validate.task.TaskManager;
import gov.nasa.pds.tools.validate.task.ValidationTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.config.ConfigParser;

/**
 * Implements a validator that validates a location (file or directory)
 * against a validation rule set. If no rule set is specified,
 * an appropriate default is chosen.
 */
public class LocationValidator {
	
	private TargetRegistrar targetRegistrar;
	private SettingsManager settingsManager;
	private ValidationRuleManager ruleManager;
	private TaskManager taskManager;
	private LabelValidator labelValidator;
	private RuleContext ruleContext;
	
	/**
	 * Creates a new instance.
	 * @throws ParserConfigurationException if a label validator cannot configure its parser
	 * @throws TransformerConfigurationException if a label validator cannot configure its transformer
	 */
	public LocationValidator() throws TransformerConfigurationException, ParserConfigurationException {
		settingsManager = SettingsManager.INSTANCE;
		taskManager = new BlockingTaskManager();
		labelValidator = new LabelValidator();
		ruleContext = new RuleContext();
		
		ConfigParser parser = new ConfigParser();
		URL commandsURL = ClassLoader.getSystemResource("validation-commands.xml");
		try {
			parser.parse(commandsURL);
		} catch (Exception e) {
			System.err.println("Could not parse validation configuration from " + commandsURL + ": " + e);
		}
		Catalog catalog = CatalogFactory.getInstance().getCatalog();
		
		ruleManager = new ValidationRuleManager(catalog);
	}
	
	/**
	 * Validates a location specified by a file or directory.
	 * 
	 * @param f the file or directory to validate
	 */
	public void validate(File f) {
		try {
			URL target = f.toURI().toURL();
			validate(target);
		} catch (MalformedURLException e) {
			// Cannot occur - a file can always be converted to
			// a URI and a URL.
		}
	}
	
	public void validate(URL target) {
		validate(new SimpleExceptionHandler(), target);
	}

	/**
	 * Validates a URL location with a given exception handler. This must be
	 * a URL that can be resolved to a file location.
	 * 
	 * @param exceptionHandler the exception handler
	 * @param url the URL to validate
	 * @throws URISyntaxException 
	 */
	public void validate(ExceptionHandler exceptionHandler, URL url) {
		File f = null;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			// Cannot happen - a URL can always be converted to a URI.
		}

		if (targetRegistrar == null) {
			System.err.println("Configuration error - targetRegistrar not specified in LocationValidator.validate()");
			return;
		}

		ValidationRule rule = getRule(f);
		if (rule != null) {
			ProblemListener listener = new ListenerExceptionPropagator(exceptionHandler);
			ValidationTask task = new ValidationTask(listener, ruleContext, targetRegistrar);
			taskManager.submit(task);
		}
	}

	/**
	 * Sets the target registrar for the next validation.
	 * 
	 * @param registrar the new target registrar
	 */
	public void setTargetRegistrar(TargetRegistrar registrar) {
		this.targetRegistrar = registrar;
	}
	
	/**
	 * Sets the task manager to use for running the validation tasks.
	 * 
	 * @param manager the new task manager
	 */
	public void setTaskManager(TaskManager manager) {
		this.taskManager = manager;
	}
	
	private ValidationRule getRule(File location) {
		String validationType = settingsManager.getString(ValidationSettings.VALIDATION_RULE, null);
		ValidationRule rule;
		
		if (validationType == null) {
			rule = ruleManager.findApplicableRule(location.getAbsolutePath());
			if (rule == null) {
				System.err.println("No validation type specified and no applicable default rules.");
			}
		} else {
			rule = ruleManager.findRuleByCaption(validationType);
			if (rule == null) {
				System.err.println("Specified validation type is invalid: " + validationType);
			}
		}
		
		return rule;
	}

	public void setModelVersion(String modelVersion) throws ValidatorException {
		labelValidator.setModelVersion(modelVersion);
	}

	public void setSchema(List<URL> schemaFiles) {
		labelValidator.setSchema(schemaFiles);
	}

	public void setSchematrons(List<Transformer> schematrons) {
		labelValidator.setSchematrons(schematrons);
	}

	public void setCachedEntityResolver(CachedEntityResolver resolver) {
		labelValidator.setCachedEntityResolver(resolver);
	}

	public void setCachedLSResourceResolver(CachedLSResourceResolver resolver) {
		labelValidator.setCachedLSResourceResolver(resolver);
	}

	public void setCatalogs(List<String> catalogFiles) {
		labelValidator.setCatalogs(catalogFiles.toArray(new String[catalogFiles.size()]));
	}

	public void setSchemaCheck(boolean value, boolean useLabelSchema) {
		labelValidator.setSchemaCheck(value, useLabelSchema);
	}

	public void setSchematronCheck(Boolean value, Boolean useLabelSchematron) {
		labelValidator.setSchematronCheck(value, useLabelSchematron);
	}

	public void addValidator(DocumentValidator validator) {
		labelValidator.addValidator(validator);
	}
	
	public void setLabelSchematrons(Map<String, Transformer> labelSchematrons) {
		labelValidator.setLabelSchematrons(labelSchematrons);
	}

	public void setForce(boolean force) {
		// TODO Auto-generated method stub
		
	}

	public void setFileFilters(List<String> regExps) {
		ruleContext.setFileFilters(regExps);
	}

	public void setRecurse(boolean traverse) {
		ruleContext.setRecursive(traverse);
	}

	/**
	 * Gets a singleton label validator.
	 * 
	 * @return the label validator
	 */
	public LabelValidator getLabelValidator() {
		return labelValidator;
	}
	
	private class ListenerExceptionPropagator implements ProblemListener {
		
		private ExceptionHandler handler;
		int errorCount;
		int warningCount;
		int infoCount;

		public ListenerExceptionPropagator(ExceptionHandler handler) {
			this.handler = handler;
		}

		@Override
		public void addProblem(ValidationProblem problem) {
			ExceptionType type;
			
			switch (problem.getProblem().getSeverity()) {
			case ERROR:
				type = ExceptionType.ERROR;
				++errorCount;
				break;
			case WARNING:
				type = ExceptionType.WARNING;
				++warningCount;
				break;
			default:
				type = ExceptionType.INFO;
				++infoCount;
				break;
			}
			LabelException ex = new LabelException(
					type,
					problem.getMessage(),
					"",
					problem.getTarget().getLocation(),
					problem.getLineNumber(),
					problem.getColumnNumber()
			);
			handler.addException(ex);
		}

		@Override
		public int getErrorCount() {
			return errorCount;
		}

		@Override
		public int getWarningCount() {
			return warningCount;
		}

		@Override
		public int getInfoCount() {
			return infoCount;
		}

		@Override
		public boolean hasProblems(String location, boolean includeChildren) {
			return false;
		}

		@Override
		public ExceptionType getSeverity(String location, boolean includeChildren) {
			return null;
		}

		@Override
		public Collection<ValidationProblem> getProblemsForLocation(
				String location, boolean includeChildren) {
			return null;
		}
		
	}
	
	/**
	 * Implements a simple exception handler that prints exceptions to the standout error output.
	 * @author merose
	 *
	 */
	private class SimpleExceptionHandler implements ExceptionHandler {

		@Override
		public void addException(LabelException exception) {
			System.err.println(exception.toString());
		}
		
	}

}
