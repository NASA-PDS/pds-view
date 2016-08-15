package gov.nasa.arc.pds.lace.shared.project;

import gov.nasa.arc.pds.lace.test.util.GetterSetterTest;

import org.testng.annotations.Test;

public class ProjectItemTest {

	@Test
	public void testGettersSetters() throws Exception {
		ProjectItem item = new ProjectItem();
		GetterSetterTest tester = new GetterSetterTest(item.getClass());
		tester.test(item);
	}
	
}
