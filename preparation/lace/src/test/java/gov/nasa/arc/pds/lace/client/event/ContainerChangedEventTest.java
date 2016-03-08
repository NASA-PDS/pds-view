package gov.nasa.arc.pds.lace.client.event;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.shared.Container;

import org.testng.annotations.Test;

public class ContainerChangedEventTest {

	@Test
	public void testConstructor() {
		Container container = new Container();
		ContainerChangedEvent event = new ContainerChangedEvent(container, true);
		assertSame(event.getData().getContainer(), container);
		assertTrue(event.getData().isRootContainer());
		
		Container newContainer = new Container();		
		event = new ContainerChangedEvent(container, newContainer);		
		assertSame(event.getData().getContainer(), container);
		assertSame(event.getData().getNewContainer(), newContainer);
		assertTrue(!event.getData().isRootContainer());
	}
}
