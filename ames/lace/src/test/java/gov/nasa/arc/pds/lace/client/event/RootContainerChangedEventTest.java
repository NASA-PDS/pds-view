package gov.nasa.arc.pds.lace.client.event;

import static org.testng.Assert.assertSame;
import gov.nasa.arc.pds.lace.shared.Container;

import org.testng.annotations.Test;

public class RootContainerChangedEventTest {

	@Test
	public void testConstructor() {
		Container container = new Container();
		RootContainerChangedEvent event = new RootContainerChangedEvent(container);
		assertSame(event.getData().getRootContainer(), container);
	}
}
