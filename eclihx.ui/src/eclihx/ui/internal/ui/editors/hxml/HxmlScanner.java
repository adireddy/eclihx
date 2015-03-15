package eclihx.ui.internal.ui.editors.hxml;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import eclihx.core.haxe.internal.parser.BuildParamParser;
import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.editors.AbstractScanner;
import eclihx.ui.internal.ui.editors.CodeWhitespaceDetector;
import eclihx.ui.internal.ui.editors.ColorManager;

/**
 * Parses hxml parameters
 */
public class HxmlScanner extends AbstractScanner {

	/**
	 * Constructor.
	 * @param colorManager manager of colors
	 * @param preferenceStore store for default properties.
	 */
	public HxmlScanner(ColorManager colorManager, IPreferenceStore preferenceStore) {
		super(colorManager, preferenceStore);
		initialize();
	}

	@Override
	protected TextAttributesKey[] getAttributesKeys() {
		TextAttributesKey[] attributeKeys = {
				new TextAttributesKey(
						PreferenceConstants.HXML_EDITOR_DEFAULT_COLOR, 
						PreferenceConstants.HXML_EDITOR_DEFAULT_BOLD, 
						PreferenceConstants.HXML_EDITOR_DEFAULT_ITALIC, 
						null, null),
				
				new TextAttributesKey(
						PreferenceConstants.HXML_EDITOR_OPTION_COLOR, 
						PreferenceConstants.HXML_EDITOR_OPTION_BOLD, 
						PreferenceConstants.HXML_EDITOR_OPTION_ITALIC, 
						null, null),
						
				new TextAttributesKey(
						PreferenceConstants.HXML_EDITOR_COMMENT_COLOR, 
						PreferenceConstants.HXML_EDITOR_COMMENT_BOLD, 
						PreferenceConstants.HXML_EDITOR_COMMENT_ITALIC, 
						null, null)
		};
		
		return attributeKeys;
	}

	@Override
	protected ArrayList<IRule> createRules() {
		
		ArrayList<IRule> rules = new ArrayList<IRule>();
		
		// Comments
		rules.add(new EndOfLineRule("#", getToken(PreferenceConstants.HXML_EDITOR_COMMENT_COLOR)));
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new CodeWhitespaceDetector()));

		IToken wordDefaultToken = getToken(PreferenceConstants.HXML_EDITOR_DEFAULT_COLOR); 
		WordRule wr = new WordRule(new IWordDetector() {			
			@Override
			public boolean isWordStart(char c) {
				return !Character.isWhitespace(c);
			}
			
			@Override
			public boolean isWordPart(char c) {
				return !Character.isWhitespace(c);
			}
		}, wordDefaultToken);
		
		IToken keyWordToken = getToken(PreferenceConstants.HXML_EDITOR_OPTION_COLOR);
		
		BuildParamParser hxmlParser = new BuildParamParser();		
		Iterable<String> paramsKeys = hxmlParser.getParametersKeys();		
		
		for (String paramKey : paramsKeys) {
			wr.addWord(paramKey, keyWordToken);
		}
		
		rules.add(wr);
			
		return rules;
	}
}
