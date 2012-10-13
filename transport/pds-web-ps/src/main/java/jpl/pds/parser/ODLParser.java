// $ANTLR 2.7.2: "odl.g" -> "ODLParser.java"$
package jpl.pds.parser;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class ODLParser extends antlr.LLkParser       implements ODLTokenTypes
 {

protected ODLParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ODLParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected ODLParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ODLParser(TokenStream lexer) {
  this(lexer,1);
}

public ODLParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void label() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST label_AST = null;
		
		try {      // for error handling
			{
			_loop3:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					expr();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3;
				}
				
			} while (true);
			}
			{
			ending();
			}
			label_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		returnAST = label_AST;
	}
	
	public final void expr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expr_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				{
				assignment();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_OBJECT:
			{
				{
				object();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_GROUP:
			{
				{
				group();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expr_AST = (AST)currentAST.root;
				break;
			}
			case POINT_OPERATOR:
			{
				{
				attachment();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expr_AST = (AST)currentAST.root;
				break;
			}
			case COMMENT:
			{
				{
				comment();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = expr_AST;
	}
	
	public final void ending() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ending_AST = null;
		
		try {      // for error handling
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp1_AST);
			match(END);
			ending_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		returnAST = ending_AST;
	}
	
	public final void assignment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_AST = null;
		
		try {      // for error handling
			{
			AST tmp2_AST = null;
			tmp2_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp2_AST);
			match(IDENT);
			}
			{
			AST tmp3_AST = null;
			tmp3_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp3_AST);
			match(ASSIGNMENT_OPERATOR);
			}
			{
			value();
			astFactory.addASTChild(currentAST, returnAST);
			}
			assignment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = assignment_AST;
	}
	
	public final void object() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST object_AST = null;
		
		try {      // for error handling
			AST tmp4_AST = null;
			tmp4_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp4_AST);
			match(LITERAL_OBJECT);
			match(ASSIGNMENT_OPERATOR);
			AST tmp6_AST = null;
			tmp6_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp6_AST);
			match(IDENT);
			{
			_loop14:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					expr();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop14;
				}
				
			} while (true);
			}
			match(LITERAL_END_OBJECT);
			{
			_loop16:
			do {
				if ((LA(1)==ASSIGNMENT_OPERATOR)) {
					endobject();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop16;
				}
				
			} while (true);
			}
			object_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = object_AST;
	}
	
	public final void group() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST group_AST = null;
		
		try {      // for error handling
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp8_AST);
			match(LITERAL_GROUP);
			match(ASSIGNMENT_OPERATOR);
			AST tmp10_AST = null;
			tmp10_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp10_AST);
			match(IDENT);
			{
			_loop20:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					expr();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop20;
				}
				
			} while (true);
			}
			match(LITERAL_END_GROUP);
			{
			_loop22:
			do {
				if ((LA(1)==ASSIGNMENT_OPERATOR)) {
					endgroup();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop22;
				}
				
			} while (true);
			}
			group_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = group_AST;
	}
	
	public final void attachment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attachment_AST = null;
		
		try {      // for error handling
			{
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp12_AST);
			match(POINT_OPERATOR);
			}
			{
			assignment();
			astFactory.addASTChild(currentAST, returnAST);
			}
			attachment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = attachment_AST;
	}
	
	public final void comment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comment_AST = null;
		
		try {      // for error handling
			{
			AST tmp13_AST = null;
			tmp13_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp13_AST);
			match(COMMENT);
			}
			comment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = comment_AST;
	}
	
	public final void endobject() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST endobject_AST = null;
		
		try {      // for error handling
			match(ASSIGNMENT_OPERATOR);
			match(IDENT);
			endobject_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_3);
		}
		returnAST = endobject_AST;
	}
	
	public final void endgroup() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST endgroup_AST = null;
		
		try {      // for error handling
			match(ASSIGNMENT_OPERATOR);
			match(IDENT);
			endgroup_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_3);
		}
		returnAST = endgroup_AST;
	}
	
	public final void value() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST value_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				{
				AST tmp18_AST = null;
				tmp18_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp18_AST);
				match(IDENT);
				}
				{
				_loop36:
				do {
					if ((LA(1)==UNITS)) {
						units();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop36;
					}
					
				} while (true);
				}
				value_AST = (AST)currentAST.root;
				break;
			}
			case QUOTED:
			{
				{
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp19_AST);
				match(QUOTED);
				}
				{
				_loop39:
				do {
					if ((LA(1)==UNITS)) {
						units();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop39;
					}
					
				} while (true);
				}
				value_AST = (AST)currentAST.root;
				break;
			}
			case SYMBOL:
			{
				{
				AST tmp20_AST = null;
				tmp20_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp20_AST);
				match(SYMBOL);
				}
				value_AST = (AST)currentAST.root;
				break;
			}
			case SEQUENCE_OPENING:
			{
				{
				list();
				astFactory.addASTChild(currentAST, returnAST);
				}
				value_AST = (AST)currentAST.root;
				break;
			}
			case SET_OPENING:
			{
				{
				set();
				astFactory.addASTChild(currentAST, returnAST);
				}
				value_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = value_AST;
	}
	
	public final void units() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST units_AST = null;
		
		try {      // for error handling
			{
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp21_AST);
			match(UNITS);
			}
			units_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_4);
		}
		returnAST = units_AST;
	}
	
	public final void list() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST list_AST = null;
		
		try {      // for error handling
			{
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(SEQUENCE_OPENING);
			}
			{
			_loop48:
			do {
				if ((LA(1)==IDENT||LA(1)==QUOTED||LA(1)==SEQUENCE_OPENING)) {
					listelements();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop48;
				}
				
			} while (true);
			}
			{
			match(SEQUENCE_CLOSING);
			}
			list_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_5);
		}
		returnAST = list_AST;
	}
	
	public final void set() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST set_AST = null;
		
		try {      // for error handling
			{
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp24_AST);
			match(SET_OPENING);
			}
			{
			_loop53:
			do {
				if ((LA(1)==IDENT||LA(1)==QUOTED||LA(1)==SEQUENCE_OPENING)) {
					listelements();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop53;
				}
				
			} while (true);
			}
			{
			match(SET_CLOSING);
			}
			set_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		returnAST = set_AST;
	}
	
	public final void listelements() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST listelements_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case IDENT:
			case QUOTED:
			{
				listitem();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEQUENCE_OPENING:
			{
				list();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop58:
			do {
				if ((LA(1)==LIST_SEPERATOR)) {
					match(LIST_SEPERATOR);
				}
				else {
					break _loop58;
				}
				
			} while (true);
			}
			listelements_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_6);
		}
		returnAST = listelements_AST;
	}
	
	public final void listitem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST listitem_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp27_AST);
				match(IDENT);
				}
				{
				_loop62:
				do {
					if ((LA(1)==UNITS)) {
						units();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop62;
					}
					
				} while (true);
				}
				listitem_AST = (AST)currentAST.root;
				break;
			}
			case QUOTED:
			{
				{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp28_AST);
				match(QUOTED);
				}
				{
				_loop65:
				do {
					if ((LA(1)==UNITS)) {
						units();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop65;
					}
					
				} while (true);
				}
				listitem_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		returnAST = listitem_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"end\"",
		"\"OBJECT\"",
		"ASSIGNMENT_OPERATOR",
		"IDENT",
		"\"END_OBJECT\"",
		"\"GROUP\"",
		"\"END_GROUP\"",
		"POINT_OPERATOR",
		"COMMENT",
		"QUOTED",
		"SYMBOL",
		"UNITS",
		"SEQUENCE_OPENING",
		"SEQUENCE_CLOSING",
		"SET_OPENING",
		"SET_CLOSING",
		"LIST_SEPERATOR",
		"WS",
		"SPECIALCHAR",
		"EOL"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 6816L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 8112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 8176L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 1818544L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 1785776L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 729216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1777792L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	
	}
