package gov.nasa.arc.pds.visualizer.server;


import gov.nasa.arc.pds.visualizer.shared.Constants;
import gov.nasa.pds.domain.PDSObject;

import org.junit.Assert;
import org.testng.annotations.Test;

public class DataServiceImplTest {

	final static String mockImageURL = Constants.moduleName + Constants.IMG_CACHE;
    final static String ARCHIVE_PATH = "../pdsObjectAccess/";

    @Test 
	public void parseDocument() throws Exception {
		
		final String file_name = "src/test/resources/dph_example_products/product_document/Product_Document_20110415.xml";
		final String expectedChild = "src/test/resources/dph_example_products/product_document/meca_rdr_sis.pdf";
		DataServiceImpl ds = new DataServiceImpl();
		
		PDSObject[] children = ds.parseDocumentProduct(ARCHIVE_PATH, file_name);	
		Assert.assertNotNull(children);
        Assert.assertEquals(5, children.length);
		Assert.assertEquals(expectedChild, (children[0]).getThis_file_name());
	}

    @Test (expectedExceptions = Exception.class)
	public void parseDocumentIncorrectAPIUsage() throws Exception {
		
		final String file_name = "src/main/resources/dph_example_products/product_document/Product_Document_20110415.xml";
		new DataServiceImpl().parseCollection(ARCHIVE_PATH, file_name);	
	}

    @Test 
	public void parseImage() throws Exception {
		
		final String file_name = "src/test/resources/dph_example_products/product_array_2d_image/Product_Array_2D_Image_20110415.xml";
		final String expectedChild = "src/test/resources/dph_example_products/product_array_2d_image/i943630r.raw";
		DataServiceImpl ds = new DataServiceImpl();
		
		PDSObject[] children = ds.parseImageProduct(ARCHIVE_PATH, file_name);	
		Assert.assertNotNull(children);
        Assert.assertEquals(1, children.length);
		Assert.assertEquals(expectedChild, (children[0]).getThis_file_name());
	}

	@Test (expectedExceptions = Exception.class)
	public void parseImageIncorrectAPIUsage() throws Exception {
		
		final String file_name = "src/test/resources/dph_example_products/product_array_2d_image/Product_Array_2D_Image_20110415.xml";
		new DataServiceImpl().parseCollection(ARCHIVE_PATH, file_name);	
	}
	
	@Test 
	public void parseTable() throws Exception {
		
		final String file_name = "src/test/resources/dph_example_products/product_table_character/Product_Table_Character_20110415.xml";
		final String expectedChild = "src/test/resources/dph_example_products/product_table_character/PDS4_ATM_TABLE_CHAR.TAB";
		DataServiceImpl ds = new DataServiceImpl();
		
		PDSObject[] children = ds.parseTableCharacterProduct(ARCHIVE_PATH, file_name);	
		Assert.assertNotNull(children);
        Assert.assertEquals(1, children.length);
		Assert.assertEquals(expectedChild, (children[0]).getThis_file_name());
	}
	
	@Test (expectedExceptions = Exception.class)
	public void parseTableIncorrectAPIUsage() throws Exception {
		
		final String file_name = "src/test/resources/dph_example_products/product_table_character/Product_Table_Character_20110415.xml";
		new DataServiceImpl().parseCollection(ARCHIVE_PATH, file_name);	
	}

	
	@Test
	public void processImageRAW() throws Exception {

		final String file_name = "src/test/resources/dph_example_products/product_array_2d_image/i943630r.raw";
	    int cols = 256;
	    int rows = 248;
		String url = new DataServiceImpl().processImage(ARCHIVE_PATH, file_name, rows, cols);	
		Assert.assertEquals(url, mockImageURL+"src/test/resources/dph_example_products/product_array_2d_image/i943630r.PNG");
	}

	@Test (expectedExceptions=Exception.class)
	public void badInputFile() throws Exception {
		final String file_name = "src/test/resources/dph_example_products/product_array_2d_image/xxx.raw";
		new DataServiceImpl().processImage(ARCHIVE_PATH, file_name, 0,0);	
	}

}
