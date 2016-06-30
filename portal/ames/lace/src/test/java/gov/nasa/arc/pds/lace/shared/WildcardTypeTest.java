package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class WildcardTypeTest {

	@Test
	public void testNamespaces() {
		List<String> namespaces = new ArrayList<String>();
		namespaces.add("ns1");
		namespaces.add("ns2");
		
		WildcardType type = new WildcardType();
		type.setNamespaces(namespaces);		
		assertEquals(type.getNamespaces(), namespaces);
	}
	
	@Test 
	public void testSuper() {		
		WildcardType type = new WildcardType();
		assertTrue(type.isWildcard());
	}
}
