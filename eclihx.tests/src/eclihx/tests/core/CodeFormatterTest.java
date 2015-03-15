/**
 * 
 */
package eclihx.tests.core;

import junit.framework.Assert;

import org.junit.Test;

import eclihx.core.haxe.model.CodeFormatter;

/**
 * Formatter tests.
 */
public class CodeFormatterTest {

	/**
	 * Test method for {@link eclihx.core.haxe.model.CodeFormatter#format(java.lang.String, eclihx.core.haxe.model.CodeFormatter.FormatOptions)}.
	 */
	@Test
	public void testFormat() {
		
		CodeFormatter.FormatOptions options = new CodeFormatter.FormatOptions();
		options.setBracketNewLines(true);
		options.setInsertTabs(false);
		options.setIntendWidth(2);
		
		String str = CodeFormatter.format(
				"class A { public static function (){ if (a==1){}}}", 
				options);
		
		String outStr = "class A\n" +
				        "{\n" +
				        "  public static function ()\n" +
				        "  {\n" +
				        "    if (a==1)\n" +
				        "    {\n" +
				        "    }\n" +
				        "  }\n" +
				        "}\n";
		
		Assert.assertEquals(outStr, str);		
	}	
}
