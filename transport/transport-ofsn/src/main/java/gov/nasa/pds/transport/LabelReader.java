package gov.nasa.pds.transport;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.CommentStatement;
import gov.nasa.pds.tools.label.GroupStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.StructurePointer;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.label.parser.LabelParser;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Class that reads a PDS label into a string buffer.
 * The actual parsing of the label is delegated to the PDS product-tools package.
 * Each LabelReader instance can be used only once.
 * 
 * @author Luca Cinquini
 *
 */
public class LabelReader {
	
	// class fields
	private static Logger logger = Logger.getLogger(LabelReader.class);
	
	private static String OBJECT_STATEMENT_CLASSNAME    = ObjectStatement.class.getSimpleName();
	private static String GROUP_STATEMENT_CLASSNAME     = GroupStatement.class.getSimpleName();
	private static String ATTRIBUTE_STATEMENT_CLASSNAME = AttributeStatement.class.getSimpleName();
	private static String POINTER_STATEMENT_CLASSNAME   = PointerStatement.class.getSimpleName();
	private static String STRUCTURE_STATEMENT_CLASSNAME = StructurePointer.class.getSimpleName();
	private static String COMMENT_STATEMENT_CLASSNAME   = CommentStatement.class.getSimpleName();
	private static String NL = "\r\n";   // use DOS <CR><LF> line terminator as it is a PDS standard
	private static String INDENT = "  "; // indentation amount for nested objects
	
	// instance fields
	private int lineNumber = 0;
	private String indent;
	private StringBuffer sb = new StringBuffer();
	
	/**
	 * Constructor reads the label into the private string buffer.
	 * @param uri: the URI location of the label to read
	 * @param indent: the starting indentation for printing each statement
	 */
	public LabelReader(URI uri, String indent) throws Exception {
		this.indent = indent;
		readLabel(uri);
	}
	
	/**
	 * Method to access the serialized label.
	 * 
	 * @return
	 */
	public StringBuffer read() {
		return sb;
	}
		
	private void readLabel(URI uri) throws Exception {
				
		// read label into object
	    ManualPathResolver resolver = new ManualPathResolver();
	    //LabelParser parser = new DefaultLabelParser(false, true, resolver);
	    //Label label = parser.parseLabel(uri.toURL(), true); // force==true
	    resolver.setBaseURI(ManualPathResolver.getBaseURI(uri));
	    
	    LabelParser parser = new DefaultLabelParser(false, true, true, resolver); // loadIncludes=false
	    Label label = parser.parseLabel(uri.toURL(), true); // force==true
	    
	    // do NOT stop if problems are encountered (force==true)
		if (label.getProblems().size()>0) {
			for (LabelParserException e : label.getProblems()) {
				logger.warn("Problem: line number="+e.getLineNumber()+", error="+e.getMessage());
			}
		}

		// serialize statements (recursively)
		this.sb = printStatements(label.getStatements());
		
		// insert "END" statement for top-level label
		if (this.indent.length()==0) {
			this.sb.append("END").append(NL);
		}
				
	}
	
	/**
	 * Method to serialize a list of statements, in the proper order.
	 * 
	 * @param statements
	 */
	private StringBuffer printStatements(List<Statement> statements) throws Exception {
		
		// sort statements in place according to line number
		// (Statement implements compareTo() method)
		Collections.sort(statements);
		
		StringBuffer out = new StringBuffer();
		
		// loop over statements
		for (Statement statement : statements) {
			
			String className = statement.getClass().getSimpleName();

			// each Statement type prints differently
			if (className.equals(OBJECT_STATEMENT_CLASSNAME)) {
				ObjectStatement _statement = (ObjectStatement)statement;
				out.append( printStatement(_statement, "OBJECT = " + _statement.getIdentifier()) );
				// increase indentation
				this.indent = this.indent + INDENT;
				out.append( printStatements(_statement.getStatements()) );
				// decrease indentation
				this.indent = this.indent.substring(0, this.indent.length()-INDENT.length());
				out.append( printStatement(_statement, "END_OBJECT = " + _statement.getIdentifier()) );
				
			} else if (className.equals(GROUP_STATEMENT_CLASSNAME)) {
				GroupStatement _statement = (GroupStatement)statement;
				out.append( printStatements(_statement.getStatements()) );
				
			} else if (className.equals(ATTRIBUTE_STATEMENT_CLASSNAME)) {
				AttributeStatement _statement = (AttributeStatement)statement;
				String value =  _statement.getValue().toString().trim();
				out.append( printStatement(_statement, _statement.getElementIdentifier()+ " = "+value) );
				
			} else if (className.equals(POINTER_STATEMENT_CLASSNAME)) {
				PointerStatement _statement = (PointerStatement)statement;
				out.append( printStatement(_statement, "^"+_statement.getIdentifier()+" = "+_statement.getValue().toString()) );
				
			} else if (className.equals(STRUCTURE_STATEMENT_CLASSNAME)) {
				StructurePointer _statement = (StructurePointer)statement;
				// do NOT print out "^STRUCTURE" statement - instead replace with expanded reference
				//out.append( printStatement(_statement, "^"+_statement.getIdentifier()+" = "+_statement.getValue().toString()) );
				out.append( printStatements(_statement.getStatements()) ); 
				
				// expand STRUCTURE pointer
				URI parenturi = _statement.getSourceURI();
				for (FileReference fref : _statement.getFileRefs()) {
					// IMPORTANT: resolves a relative URI with respect to the parent URI
					URI uri = parenturi.resolve(fref.getPath());
					out.append( (new LabelReader(uri, this.indent)).read() );
				}

			} else if (className.equals(COMMENT_STATEMENT_CLASSNAME)) {
				CommentStatement _statement = (CommentStatement)statement;
				out.append( printStatement(_statement, _statement.getText()) );
				
			} else {
				logger.error(">>>>> FOUND UNKNOWN CLASS:"+className+" <<<<<<<<<<");
			}
						
		}
		
		return out;

	}
	
	/**
	 * Prints out a single statement.
	 * 
	 * @param statement
	 * @param tostring
	 * @return
	 */
	private StringBuffer printStatement(Statement statement, String tostring) {
		
		StringBuffer sb = new StringBuffer();
		
		// insert blank lines, if necessary
		//while (this.lineNumber < statement.getLineNumber()-1) {
		//	sb.append(NL);
		//	this.lineNumber++;
		//}
		
		// print this statement
		//int lineNumber = statement.getLineNumber();
		//String className = statement.getClass().getSimpleName();
		// sb.append( lineNumber+": "+className+": "+tostring + NL );
		sb.append( this.indent + tostring + NL );
		//this.lineNumber++;
		
		// insert another new line but do NOT increment the line number
		//sb.append(NL);
		
		return sb;

	}
	
	/**
	 * Debug method.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		// parse label object
		//String uri = "file:///usr/local/pds/transport-service/testdata/CHAN_DATA_20020617.LBL";
		//String uri = "http://starbase.jpl.nasa.gov/ody-m-grs-2-edr-v1/odge1_xxxx/2002/20020617/CHAN_DATA_20020617.LBL";
		String uri = "file:///usr/local/transport-ofsn/testdata/data/vg1-j-mag-4-summ-hgcoords-48.0sec-v1.0/vg_1501/data/crs/bs2edat.lbl";
		
		StringBuffer sb = (new LabelReader(new URI(uri), "")).read();
		//System.out.println(sb.toString());
		FileUtils.writeStringToFile(new File("/tmp/bs2edat.lbl"), sb.toString());
		
	}

}