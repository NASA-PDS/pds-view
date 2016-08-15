package gov.nasa.arc.pds.lace.client.util;

import java.util.ArrayList;
import java.util.Collection;
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

	private SuggestionProvider suggestionProvider = null;
	private boolean ignoreQuery = false;

	/**
	 * Sets the object that will provide the suggestions.
	 *
	 * @param provider the new suggestion provider
	 */
	public void setSuggestionProvider(SuggestionProvider provider) {
		suggestionProvider = provider;
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		requestSuggestions(request.getQuery().trim().toLowerCase(), request, callback);
	}

	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
		setIgnoreQuery(true);
		requestSuggestions(null, request, callback);
	}

	private void requestSuggestions(String query, Request request, Callback callback) {
		int limit = request.getLimit();
		List<MySuggestion> result = new ArrayList<MySuggestion>();
		Collection<String> suggestions = null;

		if (suggestionProvider != null) {
			suggestions = suggestionProvider.getSuggestions();
		}

		int count = 0;
		for (String s : suggestions) {
			if (ignoreQuery || s.toLowerCase().contains(query)) {
				result.add(new MySuggestion(s));
				++count;
				if (count >= limit) {
					break;
				}
			}
		}

		ignoreQuery = false;
		callback.onSuggestionsReady(request, new Response(result));
	}

	/**
	 * Indicates whether to ignore the query string on the next
	 * request.
	 *
	 * @param flag true, if should ignore the query string
	 */
	public void setIgnoreQuery(boolean flag) {
		ignoreQuery = flag;
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
