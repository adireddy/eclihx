package eclihx.ui.internal.ui.editors.hx;


import java.util.ArrayList;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Scanner for breaking hx-file to non-overlapping elements of code.  
 */
public class HXPartitionScanner extends RuleBasedPartitionScanner {

	/**
	 * Constructor with partitions rules.
	 */
	public HXPartitionScanner() {
		
		IToken hxMultilineComment = new Token(IHXPartitions.HX_MULTI_LINE_COMMENT);
		IToken hxComment = new Token(IHXPartitions.HX_SINGLE_LINE_COMMENT);
		IToken hxPreprocessor = new Token(IHXPartitions.HX_PREPROCESSOR);
		IToken hxDoc = new Token(IHXPartitions.HX_DOC);
		IToken hxRegexpr = new Token(IHXPartitions.HX_REGEXPR);
		IToken hxString = new Token(IHXPartitions.HX_STRING);
		
		ArrayList<IRule> rules = new ArrayList<IRule>(5);

		rules.add(new PatternRule("/**", "*/", hxDoc, (char)0, false, true));
		rules.add(new PatternRule("/*", "*/", hxMultilineComment, (char)0, false, true));
		rules.add(new EndOfLineRule("//", hxComment));
		rules.add(new EndOfLineRule("#", hxPreprocessor));
		rules.add(new PatternRule("~/", "/", hxRegexpr, (char)0, false, true));
		rules.add(new PatternRule("\"", "\"", hxString, '\\', false, true));
		rules.add(new PatternRule("\'", "\'", hxString, '\\', false, true));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

}
