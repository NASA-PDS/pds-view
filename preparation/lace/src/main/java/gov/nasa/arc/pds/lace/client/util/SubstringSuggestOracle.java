package gov.nasa.arc.pds.lace.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Implements a suggestion oracle for autocomplete fields that
 * use a GWT {@link com.google.gwt.user.client.ui.SuggestBox}.
 * This implementation use case-insensitive substring matching
 * to find appropriate suggestions.
 *
 * <p>The suggestion oracle also supports setting a filter on
 * the available suggestions. This feature can be used, for example,
 * to restrict frame suggestions to those with a center body
 * equal to a specfied target.
 */
public class SubstringSuggestOracle extends SuggestOracle {

	private List<String> suggestions = new ArrayList<String>();

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		int limit = request.getLimit();
		String query = request.getQuery().trim().toLowerCase();

		List<MySuggestion> result = new ArrayList<MySuggestion>();
		int count = 0;
		for (String s : suggestions) {
			if (s.toLowerCase().contains(query)) {
				result.add(new MySuggestion(s));
				++count;
				if (count >= limit) {
					break;
				}
			}
		}

		callback.onSuggestionsReady(request, new Response(result));
	}

	/**
	 * Clear the list of suggestions.
	 */
	public void clear() {
		suggestions.clear();
	}

	/**
	 * Add a suggestion to the list of suggestions.
	 *
	 * @param suggestion the new suggestion
	 */
	public void add(String suggestion) {
		suggestions.add(suggestion);
	}

	private class MySuggestion implements Suggestion {

		private String suggestion;

		/**
		 * Creates a new suggestion using the given string.
		 * The same string is returned for both the display
		 * value and the replacement value.
		 *
		 * @param s the suggestion string
		 */
		public MySuggestion(String s) {
			suggestion = s;
		}

		@Override
		public String getDisplayString() {
			return suggestion;
		}

		@Override
		public String getReplacementString() {
			return suggestion;
		}

	}

}
