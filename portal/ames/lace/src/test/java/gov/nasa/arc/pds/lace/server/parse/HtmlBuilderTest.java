package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.apache.commons.lang3.StringEscapeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HtmlBuilderTest {

	private HtmlBuilder builder;

	@BeforeMethod
	public void init() {
		builder = new HtmlBuilder();
	}

	@Test
	public void testEmptyHTML() {
		// If we don't add anything, we should get null back.
		assertNull(builder.toSafeHtml());
	}

	@Test
	public void testEscapedStrings() {
		String s = "<a href='javascript:alert()'>ha ha</a><!-- unsafe script -->";
		builder.appendEscaped(s);
		assertEquals(builder.toSafeHtml(), StringEscapeUtils.escapeHtml4(s));
	}

	@Test
	public void testOpenCloseTag() {
		String unsafe = "<script type='text/javascript'>alert('ha ha!');</script>";
		String expected = "<p>" + StringEscapeUtils.escapeHtml4(unsafe) + "</p>";

		builder.beginTag("p");
		builder.appendEscaped(unsafe);
		builder.endTag();
		assertEquals(builder.toSafeHtml(), expected);
	}

	@Test
	public void testTagWithAttributes() {
		String unsafe = "<script type='text/javascript'>alert('ha ha!');</script>";
		String expected = "<a class='normal' href='#'>" + StringEscapeUtils.escapeHtml4(unsafe) + "</a>";

		builder.beginTag("a", "class='normal'", "href='#'");
		builder.appendEscaped(unsafe);
		builder.endTag();
		assertEquals(builder.toSafeHtml(), expected);
	}

	@Test
	public void testEmptyTag() {
		builder.appendEmptyTag("br");
		assertEquals(builder.toSafeHtml(), "<br/>");
	}

	@Test
	public void testEmptyTagWithAttributes() {
		builder.appendEmptyTag("br", "class='normal'");
		assertEquals(builder.toSafeHtml(), "<br class='normal'/>");
	}

	@Test(expectedExceptions={IllegalStateException.class})
	public void testCloseTooManyTags() {
		builder.endTag();
	}

	@Test(expectedExceptions={IllegalStateException.class})
	public void testTagsNotClosed() {
		builder.beginTag("p");
		@SuppressWarnings("unused")
		String html = builder.toSafeHtml();
	}

}
