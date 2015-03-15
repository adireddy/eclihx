package eclihx.tests.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.core.runtime.FileLocator;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.HaxeConfigurationList;
import eclihx.core.haxe.internal.configuration.InvalidConfigurationException;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration.Platform;
import eclihx.core.haxe.internal.parser.BuildParamParser;
import eclihx.core.util.console.parser.core.ParseError;


/**
 * Parser tests.
 * {@link eclihx.core.haxe.internal.parser.BuildParamParser}
 */
public class BuildParamParserTest {

	private static BuildParamParser parser;
	private static String executableFolder;
	
	/**
	 * Creating instance of the parser.
	 * @throws IOException Should never happen
	 * @throws MalformedURLException Should never happen
	 */
	@Before
	public void testSetup() throws MalformedURLException, IOException {
		parser = new BuildParamParser();		
		executableFolder = FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/")).getPath();
	}	

	/**
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseFile(java.lang.String, java.lang.String)}.
	 * @throws ParseError errors in parsing. 
	 * @throws InvalidConfigurationException errors in configuration.
	 * @throws IOException test bug: file can't be read
	 * @throws MalformedURLException test bug: url cannot be resolved  
	 */
	@Test
	public void testParseFile() throws ParseError, InvalidConfigurationException, MalformedURLException, IOException {
		String filePath = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/test1.hxml")).getPath();
		
		HaxeConfigurationList configurationList = parser.parseFile(filePath, executableFolder);
		HaxeConfiguration configuration = 
			configurationList.getMainConfiguration();
		
		Assert.assertNotNull(configuration);
		Assert.assertEquals("TestWhile", configuration.getStartupClass());
		Assert.assertTrue(configuration.isDebug());
		Assert.assertTrue(configuration.hasCompilationFlags("fdb"));
	}
	
	/**
	 * This test checks that parser can accept files with comments.
	 * @throws ParseError errors in parsing.
	 * @throws IOException test bug: file can't be read
	 * @throws MalformedURLException test bug: url cannot be resolved  
	 */
	@Test
	public void testParseFileWithComments() 
		throws ParseError, MalformedURLException, IOException {
		
		String path = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/testComments.hxml")).getPath();
		
		parser.parseFile(path, executableFolder);
	}
	
	/**
	 * Test file with next separator.
	 * @throws ParseError errors in parsing.
	 * @throws IOException test bug: file can't be read
	 * @throws MalformedURLException test bug: url cannot be resolved  
	 */
	@Test
	public void testParseFileWithNext() 
		throws ParseError, MalformedURLException, IOException {
		
		String path = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/withNext.hxml")).getPath();
		
		parser.parseFile(path, executableFolder);
	}

	/**
	 * Tests debug configuration 
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseString(java.lang.String, java.lang.String)}.
	 * @throws ParseError errors in parsing.
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 */
	@Test
	public void debugConfigurationParse() throws ParseError, MalformedURLException, IOException {
		parser.parseString("-debug 	-D fdb -swf Test.swf -swf-version 9", executableFolder);
	}
	
	/**
	 * Invalid configuration.
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseString(java.lang.String, java.lang.String)}.
	 * @throws ParseError errors in parsing.
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 */
	@Test
	public void badConfigurationParse() throws ParseError, MalformedURLException, IOException {
		parser.parseString("-debug -D fdb -swf Test.swf -swf-version 9 TestWhile", executableFolder);
	}
	
	/**
	 * Test --display option.
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseString(java.lang.String, java.lang.String)}.
	 * @throws ParseError errors in configuration.
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 */
	@Test
	public void testDisplayOption() throws ParseError, MalformedURLException, IOException {		
		parser.parseString("--display hihihihi@1", executableFolder);
	}
	
	/**
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseString(java.lang.String, java.lang.String)}.
	 * @throws ParseError errors in parsing.
	 * @throws InvalidConfigurationException errors in configuration.
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 */
	@Test
	public void testParse() throws ParseError, InvalidConfigurationException, MalformedURLException, IOException {
		
		String path = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/part.hxml")).getPath();
		
		HaxeConfiguration config = 
			parser.parseString(
				String.format("--no-traces --no-output -D fdb -debug %s", path), 
				executableFolder).getMainConfiguration();
		
		Assert.assertTrue(config.isDebug());
		Assert.assertTrue(config.hasCompilationFlags("fdb"));
		Assert.assertTrue(config.isNoOutputMode());
		Assert.assertTrue(config.isNoTracesMode());
	}
	
	/**
	 * Test method for {@link eclihx.core.haxe.internal.parser.BuildParamParser#parseString(java.lang.String, java.lang.String)}.
	 * Test is't ok to have a main class without -main option
	 * 
	 * @throws ParseError errors in parsing.
	 * @throws InvalidConfigurationException errors in configuration.
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 */
	@Test
	public void testSeparateMain() throws ParseError, InvalidConfigurationException, MalformedURLException, IOException
	{
		String path = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/separateMain.hxml")).getPath();
		
		HaxeConfiguration config = parser.parseFile(path, executableFolder).getMainConfiguration();
		
		Assert.assertTrue(config.getStartupClass().equals("TestCode"));
	}
	
	/**
	 * Test internal configuration file
	 * 
	 * @throws IOException {@link FileLocator#toFileURL(URL)} - if an error occurs during the conversion
	 * @throws MalformedURLException {@link URL#URL(String)} MalformedURLException - If the string specifies an unknown protocol.
	 * @throws ParseError errors in parsing.
	 * @throws InvalidConfigurationException errors in configuration.
	 */
	@Test
	public void testInternalBuildFile() throws MalformedURLException, IOException, InvalidConfigurationException, ParseError
	{
		String path = 
			FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/internalParent.hxml")).getPath();
		
		HaxeConfiguration config = parser.parseFile(path, executableFolder).getMainConfiguration();
		
		Assert.assertTrue(config.isDebug());
		Assert.assertTrue(config.getPlatform() == Platform.Flash);		
	}
	
	@Test
	public void shouldParseSwfLibFiles() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = parser.parseString("-swf-lib somenew.swf\n TestClass", executableFolder);
		
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertNotNull(config.getMainConfiguration().getFlashConfig());
		Assert.assertTrue(config.getMainConfiguration().getFlashConfig().getLibraries().size() == 1);
	}
	
	@Test
	public void shouldParseDoubleVersion() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = 
				parser.parseString(
						"-debug -D fdb -swf Test.swf -swf-version 10.2 TestWhile", 
						executableFolder);
		
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertNotNull(config.getMainConfiguration().getFlashConfig());
		Assert.assertTrue(config.getMainConfiguration().getFlashConfig().getVersion() == 10.2);
	}
	
	@Test
	public void shouldParseDeadCodeElemination() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = 
			parser.parseString(
					"--dead-code-elimination TestWhile", 
					executableFolder);
	
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertNotNull(config.getMainConfiguration().getFlashConfig());
		Assert.assertTrue(config.getMainConfiguration().isDeadCodeEliminationMode());
	}
	
	@Test
	public void shouldParseMacroses() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = 
			parser.parseString(
					"--macro Test.run() --macro Some.execute()", 
					executableFolder);
	
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertTrue(config.getMainConfiguration().getMacroCalls().contains("Test.run()"));
		Assert.assertTrue(config.getMainConfiguration().getMacroCalls().contains("Some.execute()"));
	}
	
	@Test
	public void shouldParseInterp() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = parser.parseString("--interp Test", executableFolder);
	
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertTrue(config.getMainConfiguration().isInterpModeEnabled());
	}
	
	@Test
	public void shouldParseJsNamespace() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = parser.parseString("-js ha.js --js-namespace hohoho", executableFolder);
	
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertEquals("hohoho", config.getMainConfiguration().getJSConfig().getNamespace());
	}
	
	@Test(expected = ParseError.class)
	public void shouldFailOnMultipleJsNamepaces() throws ParseError {
		parser.parseString(
				"-js ha.js --js-namespace ho --js-namespace hi",
				executableFolder);
	}
	
	@Test
	public void shouldParsePHPLibDirectory() throws ParseError, InvalidConfigurationException {
		HaxeConfigurationList config = parser.parseString("-php test --php-lib \"php\\folder\"", executableFolder);
	
		Assert.assertNotNull(config.getMainConfiguration());
		Assert.assertEquals("\"php\\folder\"", 
				config.getMainConfiguration().getPHPConfig().getLibFolderPath());
		
	}
	
	@Test(expected = ParseError.class)
	public void shouldFailOnMultiplePHPLib() throws ParseError {
		parser.parseString("-php test --php-lib \"php\\folder\" --php-lib other", 
				executableFolder);
	}
	
	@Test
	public void shouldParseRealExampleConfiguration() throws MalformedURLException, IOException, InvalidConfigurationException, ParseError {
		String path = FileLocator.toFileURL(new URL("platform:/plugin/eclihx.tests/Resources/functional/exampleConfig.hxml")).getPath();
		
		HaxeConfiguration config = parser.parseFile(path, executableFolder).getMainConfiguration();
		/*
		 * -cp src
		 * -main Main
		 * -swf9 main.swf
		 * -swf-version 9
		 * -D network-sandbox
		 * -swf-lib asset_lib.swf
		 */
		Assert.assertEquals("-main Main -D network-sandbox -cp src -debug -swf9 main.swf -swf-lib asset_lib.swf ", config.printConfiguration());	
	}
	
}
