package eclihx.ui.internal.ui.editors.hx;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import eclihx.core.haxe.internal.KeywordManager;
import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.editors.AbstractScanner;
import eclihx.ui.internal.ui.editors.AdvancedNumberRule;
import eclihx.ui.internal.ui.editors.BracketRule;
import eclihx.ui.internal.ui.editors.ColorManager;
import eclihx.ui.internal.ui.editors.CodeWhitespaceDetector;

/**
 * Scanner for haxe keywords and brackets.
 */
public class HXScanner extends AbstractScanner {
	
	@Override
	protected TextAttributesKey[] getAttributesKeys() {
		TextAttributesKey[] attributeKeys = {
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_DEFAULT_COLOR, 
					PreferenceConstants.HX_EDITOR_DEFAULT_BOLD, 
					PreferenceConstants.HX_EDITOR_DEFAULT_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_COLOR, 
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_BOLD, 
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_KEYWORDS_COLOR, 
					PreferenceConstants.HX_EDITOR_KEYWORDS_BOLD, 
					PreferenceConstants.HX_EDITOR_KEYWORDS_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_TYPE_COLOR, 
					PreferenceConstants.HX_EDITOR_TYPE_BOLD, 
					PreferenceConstants.HX_EDITOR_TYPE_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_NUMBER_COLOR, 
					PreferenceConstants.HX_EDITOR_NUMBER_BOLD, 
					PreferenceConstants.HX_EDITOR_NUMBER_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_BRACKET_COLOR, 
					PreferenceConstants.HX_EDITOR_BRACKET_BOLD, 
					PreferenceConstants.HX_EDITOR_BRACKET_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_BRACE_COLOR, 
					PreferenceConstants.HX_EDITOR_BRACE_BOLD, 
					PreferenceConstants.HX_EDITOR_BRACE_ITALIC, 
					null, null),
					
			new TextAttributesKey(
					PreferenceConstants.HX_EDITOR_STRING_COLOR, 
					PreferenceConstants.HX_EDITOR_STRING_BOLD, 
					PreferenceConstants.HX_EDITOR_STRING_ITALIC, 
					null, null),
		};
		
		return attributeKeys;
	}


	/**
	 * Constructor.
	 * @param colorManager manager of colors
	 * @param preferenceStore store for default properties.
	 */
	public HXScanner(ColorManager colorManager, IPreferenceStore preferenceStore) {
		super(colorManager, preferenceStore);
		initialize();
	}
	
	public ArrayList<IRule> createRules() {
		ArrayList<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new CodeWhitespaceDetector()));
		
		IToken bracketToken = getToken(PreferenceConstants.HX_EDITOR_BRACKET_COLOR);
		rules.add(new BracketRule('(', ')', bracketToken));
		rules.add(new BracketRule('[', ']', bracketToken));
		
		IToken braceToken = getToken(PreferenceConstants.HX_EDITOR_BRACE_COLOR);
		rules.add(new BracketRule('{', '}', braceToken));
		
		{
			// Add rule for Haxe keywords
			IToken wordToken = getToken(PreferenceConstants.HX_EDITOR_DEFAULT_COLOR); 
			WordRule wr = new WordRule(new WordDetector(), wordToken);
			
			IToken keyWordToken = getToken(PreferenceConstants.HX_EDITOR_KEYWORDS_COLOR);
			for (String keyword : KeywordManager.getNonDeclareKeywords()) {
				wr.addWord(keyword, keyWordToken);
			}
			
			IToken declareWordToken = getToken(PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_COLOR);
			for (String declareKeyword : KeywordManager.getDeclareKeywords()) {
				wr.addWord(declareKeyword, declareWordToken);
			}
			
			IToken typeConstatToken = getToken(PreferenceConstants.HX_EDITOR_NUMBER_COLOR);
			for (String typeConstantKeyword : KeywordManager.getConstantKeywords()) {
				wr.addWord(typeConstantKeyword, typeConstatToken);
			}
			
			IToken typeWordToken = getToken(PreferenceConstants.HX_EDITOR_TYPE_COLOR);
			for (String typeWord: KeywordManager.getTypeKeywords()) {
				wr.addWord(typeWord, typeWordToken);
			}
	
			rules.add(wr);
		}

		// Numbers
		rules.add(new AdvancedNumberRule(getToken(PreferenceConstants.HX_EDITOR_NUMBER_COLOR)));
		
		return rules;
	}
}
