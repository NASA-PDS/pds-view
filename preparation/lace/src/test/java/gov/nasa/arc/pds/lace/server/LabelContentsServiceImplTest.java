package gov.nasa.arc.pds.lace.server;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import gov.nasa.arc.pds.lace.shared.Container;

import java.net.URISyntaxException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class LabelContentsServiceImplTest {

	@Test
	public void testDefaultSchema() throws URISyntaxException, ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		LabelContentsServiceImpl impl = new LabelContentsServiceImpl();
		assertTrue(impl.getRootContainer("Product_Observational") != null);
	}

	@Test
	public void testGetContainerForFile() throws URISyntaxException, ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		LabelContentsServiceImpl impl = new LabelContentsServiceImpl();
		Container container = impl.getContainerForRealPath("src/test/resources/Table_Character_0300a.xml");
		assertEquals(container.getType().getElementName(), "Product_Observational");
	}

}
