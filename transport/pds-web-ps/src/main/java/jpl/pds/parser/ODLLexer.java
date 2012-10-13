// $ANTLR 2.7.2: "odl.g" -> "ODLLexer.java"$
package jpl.pds.parser;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class ODLLexer extends antlr.CharScanner implements ODLTokenTypes, TokenStream
 {
public ODLLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public ODLLexer(Reader in) {
	this(new CharBuffer(in));
}
public ODLLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public ODLLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = false;
	setCaseSensitive(false);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("end", this), new Integer(4));
	literals.put(new ANTLRHashString("END_GROUP", this), new Integer(10));
	literals.put(new ANTLRHashString("GROUP", this), new Integer(9));
	literals.put(new ANTLRHashString("END_OBJECT", this), new Integer(8));
	literals.put(new ANTLRHashString("OBJECT", this), new Integer(5));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '{':
				{
					mSET_OPENING(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mSET_CLOSING(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mSEQUENCE_OPENING(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mSEQUENCE_CLOSING(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mLIST_SEPERATOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '^':
				{
					mPOINT_OPERATOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mASSIGNMENT_OPERATOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '<':
				{
					mUNITS(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mQUOTED(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mSYMBOL(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='/') && (LA(2)=='*') && (_tokenSet_0.member(LA(3))) && (_tokenSet_0.member(LA(4))) && (true) && (true)) {
						mCOMMENT(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_1.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
						mWS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='\n'||LA(1)=='\r') && (true) && (true) && (true) && (true) && (true)) {
						mEOL(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_2.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
						mIDENT(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_3.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
						mSPECIALCHAR(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mSET_OPENING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SET_OPENING;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSET_CLOSING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SET_CLOSING;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEQUENCE_OPENING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEQUENCE_OPENING;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEQUENCE_CLOSING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEQUENCE_CLOSING;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLIST_SEPERATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LIST_SEPERATOR;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOINT_OPERATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POINT_OPERATOR;
		int _saveIndex;
		
		match('^');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mASSIGNMENT_OPERATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGNMENT_OPERATOR;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT;
		int _saveIndex;
		
		match("/*");
		{
		_loop76:
		do {
			if (((LA(1)=='*') && (_tokenSet_0.member(LA(2))) && (_tokenSet_0.member(LA(3))))&&(   LA(2)!='/' )) {
				match('*');
			}
			else if ((_tokenSet_4.member(LA(1)))) {
				{
				match(_tokenSet_4);
				}
			}
			else {
				break _loop76;
			}
			
		} while (true);
		}
		match("*/");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\u000c':
		{
			match('\f');
			break;
		}
		case '\n':  case '\r':
		{
			mEOL(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EOL;
		int _saveIndex;
		
		{
		if ((LA(1)=='\r') && (LA(2)=='\n') && (true) && (true) && (true) && (true)) {
			match("\r\n");
		}
		else if ((LA(1)=='\r') && (true) && (true) && (true) && (true) && (true)) {
			match('\r');
		}
		else if ((LA(1)=='\n')) {
			match('\n');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		newline();
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		{
		if (((LA(1)=='/'))&&( LA(2) != '*' )) {
			match('/');
		}
		else if ((_tokenSet_5.member(LA(1)))) {
			{
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '#':  case '$':  case '*':  case '+':
			case '-':  case '.':  case ':':  case '_':
			{
				mSPECIALCHAR(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		_loop83:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '/':
			{
				match('/');
				break;
			}
			case '#':  case '$':  case '*':  case '+':
			case '-':  case '.':  case ':':  case '_':
			{
				mSPECIALCHAR(false);
				break;
			}
			default:
			{
				break _loop83;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSPECIALCHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SPECIALCHAR;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '_':
		{
			match('_');
			break;
		}
		case '$':
		{
			match('$');
			break;
		}
		case '#':
		{
			match('#');
			break;
		}
		case '.':
		{
			match('.');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case ':':
		{
			match(':');
			break;
		}
		case '+':
		{
			match('+');
			break;
		}
		case '*':
		{
			match('*');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mUNITS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UNITS;
		int _saveIndex;
		
		_saveIndex=text.length();
		match('<');
		text.setLength(_saveIndex);
		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		case '#':  case '$':  case '*':  case '+':
		case '-':  case '.':  case ':':  case '_':
		{
			mSPECIALCHAR(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop87:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '#':  case '$':  case '*':  case '+':
			case '-':  case '.':  case ':':  case '_':
			{
				mSPECIALCHAR(false);
				break;
			}
			default:
			{
				break _loop87;
			}
			}
		} while (true);
		}
		_saveIndex=text.length();
		match('>');
		text.setLength(_saveIndex);
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mQUOTED(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUOTED;
		int _saveIndex;
		
		_saveIndex=text.length();
		match('"');
		text.setLength(_saveIndex);
		{
		_loop96:
		do {
			switch ( LA(1)) {
			case '\n':  case '\r':
			{
				mEOL(false);
				break;
			}
			case '\'':
			{
				match('\'');
				{
				_loop95:
				do {
					if ((_tokenSet_6.member(LA(1)))) {
						{
						match(_tokenSet_6);
						}
					}
					else {
						break _loop95;
					}
					
				} while (true);
				}
				match('\'');
				break;
			}
			default:
				if ((_tokenSet_7.member(LA(1)))) {
					{
					match(_tokenSet_7);
					}
				}
			else {
				break _loop96;
			}
			}
		} while (true);
		}
		_saveIndex=text.length();
		match('"');
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSYMBOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SYMBOL;
		int _saveIndex;
		
		_saveIndex=text.length();
		match('\'');
		text.setLength(_saveIndex);
		{
		_loop100:
		do {
			if ((_tokenSet_8.member(LA(1)))) {
				{
				match(_tokenSet_8);
				}
			}
			else {
				break _loop100;
			}
			
		} while (true);
		}
		_saveIndex=text.length();
		match('\'');
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-9217L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 4294981120L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 576438865150083072L, 576460745860972544L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 288349226486726656L, 2147483648L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=-4398046520321L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 576298127661727744L, 576460745860972544L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-549755823105L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=-566935692289L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=-549755823105L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	
	}
