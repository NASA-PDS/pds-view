package gov.nasa.arc.pds.lace.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.MessageTranslator.Translation;

import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;

@Guice(modules = {MessageTranslatorTest.Module.class})
public class MessageTranslatorTest {

	@Inject
	private Provider<MessageTranslator> translatorProvider;
	
	private MessageTranslator translator;
	
	@BeforeMethod
	public void init() {
		translator = translatorProvider.get();
	}
	
	@Test
	public void testMatchingMessage() {
		String message = "cvc-complex-type.2.4.b: The content of element 'THE_ELEMENT' is not complete. One of 'A, B, C' is expected.";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertFalse(translation.shouldOmit());
		String result = translation.translate(message);
		
		// Make sure a translation occurred.
		assertTrue(result.contains("missing"));
		assertTrue(result.contains("THE_ELEMENT"));
	}
	
	@Test
	public void testNonmatchingMessage() {
		String message = "does not match any pattern";
		Translation translation = translator.translate(message);
		assertNull(translation);
	}
	
	@Test
	public void testMessageWithSpecialCharacters() {
		String message = "cvc-complex-type.2.2: Element 'start_date_time' must have no element [children], and the value must be valid.";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertFalse(translation.shouldOmit());
		String result = translation.translate(message);

		// Make sure a translation happened, and that the element name was included.
		assertNotEquals(result, message);
		assertTrue(result.contains("start_date_time"));
	}
	
	@Test
	public void testDefaultTranslation() {
		String message = "the-error-message-id: The message";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertFalse(translation.shouldOmit());
		String result = translation.translate(message);
		
		// Make sure the error message ID was stripped.
		assertEquals(result, "The message");
	}
	
	@Test
	public void testAttributeMessage() {
		// A message matching an actual pattern from MessageTranslator.properties.
		String message = "cvc-attribute.3: The value 'xyz' of attribute '@bar' on element 'foo' is not valid with respect to its type, 'double'.";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertFalse(translation.shouldOmit());
		assertEquals(translation.getAttributeName(message), "bar");
	}
	
	@Test
	public void testOmitMessage() {
		// A message matching an actual pattern from MessageTranslator.properties that should be omitted.
		String message = "cvc-type.3.1.3: The value 'xyz' of element 'foo' is not valid.";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertTrue(translation.shouldOmit());
	}
	
	@Test
	public void testMatchingValue() {
		// A message matching an actual pattern from MessageTranslator.properties that has a value specification.
		String message = "cvc-minLength-valid: Value 'foo' with length = '3' is not facet-valid with respect to minLength '5' for type 'bar'.";
		Translation translation = translator.translate(message);
		
		assertNotNull(translation);
		assertFalse(translation.shouldOmit());
		assertEquals(translation.getValue(message), "foo");
	}
	
	public static class Module extends AbstractModule {
		@Override
		protected void configure() {
			// No special bindings needed
		}
	}
	
}
