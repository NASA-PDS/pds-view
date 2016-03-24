package gov.nasa.pds.tools.validate.rule;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Tests that a file has only legal UTF-8 byte sequences.
 */
public class UTF8ByteStreamRule extends AbstractValidationRule {

	@ValidationTest
	public void testUTF8ByteStream() {
		BufferedInputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(getTarget()));
			checkUTF8ByteStream(in);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot read target: " + getTarget().getAbsolutePath());
		} catch (IOException e) {
			reportError(GenericProblems.MALFORMED_UTF8_CHARACTER, getTarget(), -1, -1);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					// Ignore - no way to recover
				}
			}
		}
	}

	/**
	 * Checks that an input stream only has properly formed UTF-8
	 * byte sequences.
	 *
	 * @param in the input stream
	 * @throws IOException if there is an error while reading the input stream
	 *   or if there is an invalid byte sequence
	 */
	private void checkUTF8ByteStream(BufferedInputStream in) throws IOException {
		int c;

		for (;;) {
			c = in.read();
			if (c < 0) {
				break;
			}

			// If this is an extension character, it is in an invalid spot.
			// Otherwise, if it's the start of a multi-byte sequence, check
			// that we have the right number of extension characters.
			if ((c & 0xC0) == 0x80) {
				// Extension character not at start of multi-byte sequence.
				throw new IOException("Invalid UTF-8 byte sequence");
			} else if ((c & 0xE0) == 0xC0) {
				checkExtensions(in, 1);
			} else if ((c & 0xF0) == 0xE0) {
				checkExtensions(in, 2);
			} else if ((c & 0xF8) == 0xF0) {
				checkExtensions(in, 3);
			} else if ((c & 0xFC) == 0xF8) {
				checkExtensions(in, 4);
			} else if ((c & 0xFE) == 0xFC) {
				checkExtensions(in, 5);
			} else if (c > 0x7F) {
				throw new IOException("Invalid UTF-8 byte sequence");
			}
		}
	}

	/**
	 * Checks that we have the right number of extension characters in
	 * a multi-byte sequence.
	 *
	 * @param in the input stream
	 * @param n the number of extension characters expected
	 * @throws IOException if an exception occurs while reading the input
	 *   or if there is an invalid byte sequence
	 */
	private void checkExtensions(BufferedInputStream in, int n) throws IOException {
		for (int i=0; i < n; ++i) {
			int c = in.read();
			if (c < 0 || (c & 0xC0) != 0x80) {
				throw new IOException("Invalid UTF-8 byte sequence");
			}
		}
	}

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);
		return f.isFile() && f.canRead();
	}

}
