//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generation;

import gov.nasa.pds.imaging.generation.cli.options.Flag;
import gov.nasa.pds.imaging.generation.cli.options.InvalidOptionException;
import gov.nasa.pds.imaging.generation.generate.GeneratedElements;
import gov.nasa.pds.imaging.generation.label.PDS3Label;
import gov.nasa.pds.imaging.generation.label.PDSObject;
import gov.nasa.pds.imaging.generation.util.ToolInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.xml.sax.SAXException;

public class GenerationLauncher {
	private PDSObject pdsObject = null;
    
    private String filePath = null;
    
    //private Map<String,Map> mapped = null;
    
    //private VelocityContext context;
    //private String templatePath;
    private File templateFile;
    //private Template template;
    private File outputFile;
    private boolean debug;
    Generator translator;
    private List<String> contextList;
    
    public GenerationLauncher() {
		//this.context = null;
		//this.templatePath = "";
		this.templateFile = null;
		//this.template = null;
		this.pdsObject = null;
		this.filePath = null;
		this.outputFile = null;
		this.debug = false;
		this.translator = null;
		this.contextList = new ArrayList<String>();
    }
    
    /**
     * A method to parse the command-line arguments.
     *
     * @param args The command-line arguments
     * @return A class representation of the command-line arguments
     *
     * @throws ParseException If there was an error during parsing.
     */
    public final CommandLine parse(final String[] args)
    throws ParseException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(Flag.getOptions(), args);
    }
    
    /**
     * Examines the command-line arguments passed into the Harvest Tool
     * and takes the appropriate action based on what flags were set.
     *
     * @param line A class representation of the command-line arguments.
     *
     * @throws Exception If there was an error while querying the options
     * that were set on the command-line.
     */
    public final void query(final CommandLine line) throws Exception {    	
        List<Option> processedOptions = Arrays.asList(line.getOptions());
        for (Option o : processedOptions) {
            if (o.getOpt().equals(Flag.HELP.getShortName())) {
                displayHelp();
                System.exit(0);
            } else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
                displayVersion();
                System.exit(0);
            } else if (o.getOpt().equals(Flag.PDS3.getShortName())) {
            	this.pdsObject = new PDS3Label(o.getValue().trim());
            	//this.contextList.add(PDS3Label.CONTEXT);	// Add PDS3Label context since PDS3 Label is included on command-line
            	//translator.setPDSObject(object);
                //this.pds3Label = new PDS3Label(o.getValue().trim());
            	//this.label = new PDS3Label("src/main/resources/mer/1p216067135edn76pop2102l2m1.img");
            } else if (o.getOpt().equals(Flag.TEMPLATE.getShortName())) {
            	this.templateFile = new File(o.getValue().trim());
                //System.out.println(this.templatePath);
            	//this.templatePath = "/Users/jpadams/mer_template.vm";
            } else if (o.getOpt().equals(Flag.FILE.getShortName())) {
                this.filePath = o.getValue();
            } else if (o.getOpt().equals(Flag.OUTPUT.getShortName())) {
                this.outputFile = new File(o.getValue());
            } else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
                this.debug = true;
            }
        }
        
        if (this.pdsObject == null) {	// Throw error if no PDS3 label is specified
        	throw new InvalidOptionException("PDS3 label must be specified.");
        }
        if (this.templateFile == null) {	// Throw error if no template file specified
        	throw new InvalidOptionException("Template file must be specified.");
        }
        if (this.outputFile == null) {	// Need to set output filename based on label filename
        	this.outputFile = new File(this.pdsObject.getFilePath() + "_pds4");	// TODO Currently just add _pds4 to pds3 label name
        }
        
        this.translator = new Generator(this.pdsObject, this.templateFile, this.filePath, this.outputFile);
        
    }
    
    public final void generate() throws TemplateException, IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException, TransformerException, Exception {
    	this.translator.generate(this.debug);
    }
    
    /**
     * Displays the current version and disclaimer notice.
     *
     */
    public final void displayVersion() {
        System.err.println("\n" + ToolInfo.getName());
        System.err.println(ToolInfo.getVersion());
        System.err.println("Release Date: " + ToolInfo.getReleaseDate());
        System.err.println(ToolInfo.getCopyright() + "\n");
    }

    /**
     * Displays tool usage.
     *
     */
    public final void displayHelp() {
        int maxWidth = 80;
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(maxWidth, "PDS4Transform <policy file> <options>",
                null, Flag.getOptions(), null);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
        	System.out.println("\nType 'PDS4Transform -h' for usage");
            System.exit(0);
       	}
        try {
            GenerationLauncher launcher = new GenerationLauncher();
            CommandLine commandline = launcher.parse(args);
            launcher.query(commandline);
            launcher.generate();
            //launcher.closeHandlers();
        } catch (ParseException pEx) {
            System.err.println("Command-line parse failure: "
                    + pEx.getMessage());
            System.exit(1);
        } catch (Exception e) {
          e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
    	
    	
       /* PDS3Label label = new PDS3Label(args[0]);
        //System.out.println("-----------Original Label------------");
        //System.out.println(label.toString());
        
        PDS4Transform transform;
        try {
            transform = new PDS4Transform(label, 
            		new File(args[1]),
            		args[2]);

        System.out.println("-----------New Label----------");
        //PDS4Label newLabel = transform.getNewLabel();
        transform.display();
        //newLabel.toString();
        //System.out.println(newLabel.clean());
        
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Label normLabel = norm.getNormalizedLabel();
        //System.out.println("-----------Normalized Label----------");
        //System.out.println(normLabel.toString());
        
        // Display output for SQL insert - Can probably delete
        /*System.out.println();
        Map<String,Map> mapped = norm.getSchemaMappedLabel();
        for (Iterator it = mapped.keySet().iterator(); it.hasNext();) {
            String tab = (String)it.next();
            System.out.println("insert into "+tab);
            StringBuffer colNames = new StringBuffer();
            StringBuffer values = new StringBuffer();
            Map map = mapped.get(tab);
            for (Iterator it2 = map.keySet().iterator(); it2.hasNext(); ) {
                String colt = (String)it2.next();
                colNames.append(colt+",");
                values.append(map.get(colt)+",");
                System.out.println("\t"+colt+"="+map.get(colt));
            }
            
            // remove the last ","
            System.out.println("("+colNames.substring(0,colNames.length()-1)+")");
            System.out.println("values ("+values.substring(0,values.length()-1)+")");
        }*/
        


    }

}
