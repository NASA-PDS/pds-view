//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.registry.model.naming;

import gov.nasa.pds.registry.model.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*; //import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pramirez
 * 
 */
public class DefaultVersionerTest {
	Versioner versioner;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.versioner = new DefaultVersioner();
	}

	@Test
	public void testComparator() {
		Comparator<RegistryObject> comparator = versioner.getComparator();
		List<RegistryObject> objects = new ArrayList<RegistryObject>();
		RegistryObject ro1 = new RegistryObject("1", "testRegistry", "1",
				"test 1.0", RegistryObject.class.getName());
		ro1.setVersionName("1.0");
		objects.add(0, ro1);
		RegistryObject ro2 = new RegistryObject("2", "testRegistry", "1",
				"test 1.2", RegistryObject.class.getName());
		ro2.setVersionName("1.2");
		objects.add(0, ro2);
		RegistryObject ro3 = new RegistryObject("3", "testRegistry", "1",
				"test 2.0", RegistryObject.class.getName());
		ro3.setVersionName("2.0");
		objects.add(0, ro3);
		RegistryObject ro4 = new RegistryObject("4", "testRegistry", "1",
				"test 1.1", RegistryObject.class.getName());
		ro4.setVersionName("1.1");
		objects.add(0, ro4);
		RegistryObject ro5 = new RegistryObject("5", "testRegistry", "1",
				"test 2.1", RegistryObject.class.getName());
		ro5.setVersionName("2.1");
		objects.add(0, ro5);
		RegistryObject ro6 = new RegistryObject("6", "testRegistry", "1",
				"test 3.0", RegistryObject.class.getName());
		ro6.setVersionName("3.0");
		objects.add(0, ro6);
		RegistryObject ro7 = new RegistryObject("7", "testRegistry", "1",
				"test 3.0", RegistryObject.class.getName());
		ro7.setVersionName("3.0");
		objects.add(0, ro6);
		Collections.sort(objects, comparator);
		assertThat(objects.get(0).getVersionName(), is("1.0"));
		assertThat(objects.get(1).getVersionName(), is("1.1"));
		assertThat(objects.get(2).getVersionName(), is("1.2"));
		assertThat(objects.get(3).getVersionName(), is("2.0"));
		assertThat(objects.get(4).getVersionName(), is("2.1"));
		assertThat(objects.get(5).getVersionName(), is("3.0"));
		assertThat(objects.get(6).getVersionName(), is("3.0"));
	}

	@Test
	public void testVersionGeneration() {
		String initialVersion = versioner.getInitialVersion();
		assertThat(initialVersion, is("1.0"));
		assertThat(versioner.getNextVersion(initialVersion), is("2.0"));
		assertThat(versioner.getNextVersion(initialVersion, true), is("2.0"));
		assertThat(versioner.getNextVersion(initialVersion, false), is("1.1"));
		assertThat(versioner.getNextVersion("2.1"), is("3.0"));
		assertThat(versioner.getNextVersion(null), is(nullValue()));
	}

}
