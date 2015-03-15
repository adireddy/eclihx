package eclihx.core.haxe.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

/**
 * Class will manage Haxe keywords.
 */
public final class KeywordManager {
	
	private enum KeywordGroup {
		Declare,
		StandardTypes,
		TypeConstants,
		Other
	}
	
	private final static HashMap<KeywordGroup, List<String>> keywordCollection = new HashMap<KeywordGroup, List<String>>(); 
	
	static {
		final String[] declareKeywords = {
				"class", "function", "interface", "new", "package", "var"};
		keywordCollection.put(KeywordGroup.Declare, Arrays.asList(declareKeywords));
		
		final String[] otherKeywords = {
				"break", "callback", "case", "cast", "catch", /*"class",*/ "continue", 
				"default", "do", "dynamic", "else", "enum", "extends", "extern", 
				"for", /*"function",*/ "if", "implements", "import", "in", "inline", 
				/*"interface",*/ /*"new",*/ "override", /*"package",*/ "private", "public", 
				"return", "static", "switch", "this", "throw", "try", "typedef", 
				"untyped", "using", /*"var",*/ "while"};
		keywordCollection.put(KeywordGroup.Other, Arrays.asList(otherKeywords));
		
		final String[] typeWords = {"Array", "Bool", "Class", "Date", "Dynamic", "Enum", 
				"Float", "Hash", "Int", "Iterable", "Iterator", "List", "String", 
				"StringBuf", "Type", "UInt", "Void"};
		keywordCollection.put(KeywordGroup.StandardTypes, Arrays.asList(typeWords));
		
		final String[] typeConstants = {"null", "true", "false"};
		keywordCollection.put(KeywordGroup.TypeConstants, Arrays.asList(typeConstants));
	}
	
	/**
	 * Get the declare keywords.
	 * @return list of declare keywords.
	 */
	public static Iterable<String> getDeclareKeywords() {
		return keywordCollection.get(KeywordGroup.Declare);
	}
	
	/**
	 * Get the non-declare keywords.
	 * @return list of non-declare keywords.
	 */
	public static Iterable<String> getNonDeclareKeywords() {
		return keywordCollection.get(KeywordGroup.Other);
	}
	
	/**
	 * Get all declare keywords.
	 * @return list of all keywords.
	 */
	public static Iterable<String> getAllKeywords() {
		List<String> all = new ArrayList<String>();
		all.addAll(keywordCollection.get(KeywordGroup.Declare));
		all.addAll(keywordCollection.get(KeywordGroup.Other));
		return all;
	}
	
	/**
	 * Get the type words.
	 * @return list of type words.
	 */
	public static Iterable<String> getTypeKeywords() {
		return keywordCollection.get(KeywordGroup.StandardTypes);
	}
	
	/**
	 * Get the constant keywords.
	 * @return list of constant keywords.
	 */
	public static Iterable<String> getConstantKeywords() {
		return keywordCollection.get(KeywordGroup.TypeConstants);
	}
}
