package gov.nasa.pds.imaging.generation.generate;

import java.io.File;
import java.util.HashMap;

import gov.nasa.pds.imaging.generation.TemplateException;
import gov.nasa.pds.imaging.generation.context.PDSContext;
import gov.nasa.pds.imaging.generation.generate.elements.Element;
import gov.nasa.pds.imaging.generation.label.PDS3Label;
import gov.nasa.pds.imaging.generation.label.PDSObject;
import gov.nasa.pds.imaging.generation.util.XMLUtil;

public class GeneratedElements implements PDSContext {
	public static final String CONTEXT = "generate";
	
    public static final String XML_FILENAME = "../conf/generated-mappings.xml";
    
    /** XML element name holding the key value **/
    public static final String XML_KEY = "context";
    
    /** XML element name holding the mapped value **/
    public static final String XML_VALUE = "class";
    
    
    public HashMap<String, Class<?>> genValsMap = new HashMap<String, Class<?>>();
	
	public GeneratedElements() throws TemplateException, Exception {
		this.genValsMap.putAll(XMLUtil.getGeneratedMappings(XML_FILENAME, XML_KEY, XML_VALUE));
	}
	
    public void addMapping(String key, Class<?> value) {
    	this.genValsMap.put(key, value);
    }
	
	@Override
	public String get(String value) {
		Element el;
		try {
			el = (Element) this.genValsMap.get(value).newInstance();
			return el.getValue();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getContext() {
		return CONTEXT;
	}

	@Override
	public String getUnits(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
