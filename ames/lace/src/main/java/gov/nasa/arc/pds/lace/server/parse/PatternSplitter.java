package gov.nasa.arc.pds.lace.server.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of a finite state machine that splits regular
 * expression patterns at top-level conjunction operators.
 */
public class PatternSplitter {

	/**
	 * Splits a regular expression pattern at top-level uses of the
	 * conjunction operator, "|".
	 *
	 * @param s the pattern to split
	 * @return a collection of subpatterns
	 */
	static Collection<String> splitPattern(String s) {
		List<String> patterns = new ArrayList<String>();
		int nesting = 0;
		boolean inEscape = false;
		int patternStart = 0;

		for (int i=0; i < s.length(); ++i) {
			if (inEscape) {
				inEscape = false;
			} else {
				switch (s.charAt(i)) {
				case '(': //FALLTHROUGH
				case '[': //FALLTHROUGH
				case '{':
					++nesting;
					break;

				case ')': //FALLTHROUGH
				case ']': //FALLTHROUGH
				case '}':
					--nesting;
					if (nesting < 0) {
						nesting = 0;
					}
					break;

				case '\\':
					inEscape = true;
					break;

				case '|':
					if (nesting <= 0) {
						patterns.add(s.substring(patternStart, i));
						patternStart = i+1;
					}
					break;

				default:
					// do nothing
					break;
				}
			}
		}

		if (patternStart < s.length()) {
			patterns.add(s.substring(patternStart));
		}

		if (patterns.size() == 0) {
			patterns.add("");
		}

		return patterns;
	}

}
