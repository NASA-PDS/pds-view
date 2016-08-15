package gov.nasa.arc.pds.lace.server.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Implements a utility that creates HTML documentation.
 */
public class HtmlBuilder {

	private static final String OPEN_TAG_START = "<";
	private static final String CLOSE_TAG_START = "</";
	private static final String TAG_END = ">";
	private static final String EMPTY_TAG_END = "/>";

	private StringBuilder builder = new StringBuilder();
	private List<String> tagStack = new ArrayList<String>();
	private boolean isEmpty = true;

	/**
	 * Adds a string that should not contain HTML, by first
	 * escaping any HTML special characters.
	 *
	 * @param s the string to append
	 */
	public void appendEscaped(String s) {
		builder.append(StringEscapeUtils.escapeHtml4(s));
		isEmpty = false;
	}

	/**
	 * Begins an HTML tag.
	 *
	 * @param tag the tag name
	 * @param attrs an array of HTML attribute assignments for the tag
	 */
	public void beginTag(String tag, String... attrs) {
		builder.append(OPEN_TAG_START);
		builder.append(tag);
		addAttributes(builder, attrs);
		builder.append(TAG_END);

		tagStack.add(tag);
		isEmpty = false;
	}

	/**
	 * Ends the current HTML tag.
	 */
	public void endTag() {
		if (tagStack.isEmpty()) {
			throw new IllegalStateException("Trying to close a tag when none are open");
		}
		String tag = tagStack.remove(tagStack.size() - 1);
		builder.append(CLOSE_TAG_START);
		builder.append(tag);
		builder.append(TAG_END);
		isEmpty = false;
	}

	/**
	 * Appends an empty HTML tag.
	 *
	 * @param tag the tag name
	 * @param attrs an array of HTML attribute assignments
	 */
	public void appendEmptyTag(String tag, String... attrs) {
		builder.append(OPEN_TAG_START);
		builder.append(tag);
		addAttributes(builder, attrs);
		builder.append(EMPTY_TAG_END);

		isEmpty = false;
	}

	private void addAttributes(StringBuilder b, String... attrs) {
		for (String s : attrs) {
			b.append(' ');
			b.append(s);
		}
	}

	/**
	 * Gets the sanitized HTML result.
	 *
	 * @return the built HTML string, or null, if no content has been added
	 */
	public String toSafeHtml() {
		if (!tagStack.isEmpty()) {
			throw new IllegalStateException("Tag stack is not empty (" + tagStack.size() + " unclosed tags)");
		}
		if (isEmpty) {
			return null;
		} else {
			return builder.toString();
		}
	}

	/**
	 * Tests whether anything has been written to the builder.
	 *
	 * @return true, if the content is empty
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

}
