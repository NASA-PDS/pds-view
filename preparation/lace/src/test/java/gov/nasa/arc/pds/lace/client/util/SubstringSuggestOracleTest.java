package gov.nasa.arc.pds.lace.client.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SubstringSuggestOracleTest {

	private static final String[] SUGGESTIONS = { "aardvarck", "banana", "bandana", "george" };

	private SubstringSuggestOracle oracle;

	@BeforeMethod
	public void init() {
		oracle = new SubstringSuggestOracle();
	}

	@Test(dataProvider="queryTests")
	public void testEmptySuggestions(String query, int limit, String[] expected) {
		String[] results = getSuggestions(query, limit);
		// Should always get no suggestions.
		assertEqualsNoOrder(results, new String[0]);
	}

	@Test(dataProvider="queryTests")
	public void testSuggestions(String query, int limit, String[] expected) {
		addSuggestions();
		String[] results = getSuggestions(query, limit);
		assertEqualsNoOrder(results, expected);

		oracle.clear();
		results = getSuggestions(query, limit);
		// Should always get no suggestions.
		assertEqualsNoOrder(results, new String[0]);
	}

	@Test(dataProvider="queryTests")
	public void testSuggestionsAsCollection(String query, int limit, String[] expected) {
		addSuggestions();
		String[] results = getSuggestions(query, limit);
		assertEqualsNoOrder(results, expected);

		oracle.clear();
		results = getSuggestions(query, limit);
		// Should always get no suggestions.
		assertEqualsNoOrder(results, new String[0]);
	}

	@Test
	public void testDisplayAndReplacementString() {
		addSuggestions();
		Request request = new Request();
		request.setQuery("BANDANA");

		Callback specialCallback = new Callback() {
			@Override
			public void onSuggestionsReady(Request request, Response response) {
				assertEquals(response.getSuggestions().size(), 1);
				for (Suggestion suggestion : response.getSuggestions()) {
					assertEquals(suggestion.getDisplayString(), suggestion.getReplacementString());
				}
			}
		};

		oracle.requestSuggestions(request, specialCallback);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="queryTests")
	private Object[][] getQueryTests() {
		return new Object[][] {
				// query, limit, [] suggestions
				{ "a", -1, new String[] { "aardvarck", "banana", "bandana" } },
				{ "b", -1, new String[] { "banana", "bandana" } },
				{ "b", 1, new String[] { "banana" } },
				{ "g", -1, new String[] { "george" } },
				{ "ban", -1, new String[] { "banana", "bandana" } },
				{ "band", -1, new String[] { "bandana" } },
				{ "z", -1, new String[0] },

				// Test queries with leading and trailing spaces. (GEO-403)
				{ " a ", -1, new String[] { "aardvarck", "banana", "bandana" } },
				{ " band ", -1, new String[] { "bandana" } },
		};
	}

	private void addSuggestions() {
		oracle.clear();
		for (String suggestion : SUGGESTIONS) {
			oracle.add(suggestion);
		}
	}

	private String[] getSuggestions(String query, int limit) {
		Request request = new Request();
		request.setQuery(query);
		if (limit > 0) {
			request.setLimit(limit);
		}

		MyCallback callback = new MyCallback();
		oracle.requestSuggestions(request, callback);
		return callback.getSuggestions();
	}

	private static class MyCallback implements Callback {

		private List<String> suggestions = new ArrayList<String>();

		@Override
		public void onSuggestionsReady(Request request, Response response) {
			for (Suggestion suggestion : response.getSuggestions()) {
				suggestions.add(suggestion.getDisplayString());
			}
		}

		public String[] getSuggestions() {
			return suggestions.toArray(new String[0]);
		}

	}

}
