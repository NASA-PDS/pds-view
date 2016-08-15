package gov.nasa.arc.pds.lace.client.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 */
public class IconLookup {	
	private static final Map<String, String> COMPLEX_ITEM_TYPE_ICON = new HashMap<String, String>();	
	
	private static final String CUBE_CLASSNAME = "cube";	
	private static final String ARRAY_2D_CLASSNAME = "array2D";	
	private static final String ARRAY_ND_CLASSNAME = "arrayND";
	private static final String IMAGE_2D_CLASSNAME = "image2D";
	private static final String IMAGE_ND_CLASSNAME = "imageND";
	private static final String SPECTRUM_2D_CLASSNAME = "spectrum2D";
	private static final String SPECTRUM_ND_CLASSNAME = "spectrumND";
	private static final String MAP_CLASSNAME = "map";
	private static final String GENERIC_DATA_CLASSNAME = "data";
	private static final String TABLE_CLASSNAME = "table";
	private static final String FIELD_CLASSNAME = "field";
	private static final String FILE_CLASSNAME = "file";
	private static final String TEXT_CLASSNAME = "text";
	
	static {
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Product_Observational"), CUBE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Identification_Area"), CUBE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Observation_Area"), CUBE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Reference_List"), CUBE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("File_Area_Observational"), CUBE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("File_Area_Observational_Supplemental"), CUBE_CLASSNAME);
		
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Table_Binary"), TABLE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Table_Character"), TABLE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Table_Delimited"), TABLE_CLASSNAME);
				
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_2D_Image"), IMAGE_2D_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_3D_Image"), IMAGE_ND_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_2D"), ARRAY_2D_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_3D"), ARRAY_ND_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_2D_Spectrum"), SPECTRUM_2D_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_3D_Spectrum"), SPECTRUM_ND_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Array_2D_Map"), MAP_CLASSNAME);
		
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Field_Binary"), FIELD_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Group_Field_Binary"), FIELD_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Field_Character"), FIELD_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Group_Field_Character"), FIELD_CLASSNAME);
		
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("File"), FILE_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Stream_Text"), TEXT_CLASSNAME);		
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Header"), GENERIC_DATA_CLASSNAME);
		COMPLEX_ITEM_TYPE_ICON.put(makeKey("Header_Encoded"), GENERIC_DATA_CLASSNAME);		
	}

	/**
	 * Gets the CSS class name of the icon that represents the specified name.
	 * 
	 * @param name the element name
	 * @return the CSS class name
	 */
	public String getIconClassName(String name) {
		String className = COMPLEX_ITEM_TYPE_ICON.get(makeKey(name));		
		if (className == null) {
			return CUBE_CLASSNAME;
		}
		return className;
	}

	private static String makeKey(String name) {
		return name;
	}
}
