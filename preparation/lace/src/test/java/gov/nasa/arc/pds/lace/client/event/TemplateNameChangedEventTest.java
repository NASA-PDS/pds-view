package gov.nasa.arc.pds.lace.client.event;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TemplateNameChangedEventTest {

	@Test
	public void testConstructor() {
		TemplateNameChangedEvent event = new TemplateNameChangedEvent("hello");
		assertEquals(event.getData(), "hello");
	}

}
