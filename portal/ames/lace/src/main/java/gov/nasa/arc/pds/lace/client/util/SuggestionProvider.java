package gov.nasa.arc.pds.lace.client.util;

import java.util.Collection;

/**
 * Implements a callback interface that the {@link SubstringSuggestOracle} uses
 * to get suggestions, when needed.
 */
public interface SuggestionProvider {

	/**
	 * Gets the suggestion strings.
	 *
	 * @return a collection of suggestions
	 */
	Collection<String> getSuggestions();

}
