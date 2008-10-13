package eclihx.tests.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.InvalidConfigurationException;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration.Platform;

/**
 * Test for eclihx.core.haxe.internal.configuration.HaxeConfiguration.	
 */
public class HaxeConfigurationTest {

	/**
	 * Field with the test configuration.
	 */
	private HaxeConfiguration configuration;
	
	/**
	 * Setting up.
	 * @throws java.lang.Exception exception.
	 */
	@Before
	public void setUp() throws Exception {
		configuration = new HaxeConfiguration();
	}

	/**
	 * Test method for {@link eclihx.core.haxe.internal.configuration.HaxeConfiguration#printConfiguration()}.
	 * @throws InvalidConfigurationException If configuration is invalid.
	 */
	@Test
	public void testPrintConfigurationSimple() 
		throws InvalidConfigurationException {
		
		configuration.enableDebug();
		configuration.setStartupClass("TestWhile");
		
		Assert.assertEquals(
			"-main TestWhile -debug ", configuration.printConfiguration());
	
	}
	
	/**
	 * Checks printing of the source directories.
	 * Test method for {@link eclihx.core.haxe.internal.configuration.HaxeConfiguration#printConfiguration()}.
	 * @throws InvalidConfigurationException If configuration is invalid.
	 */
	@Test
	public void testPrintConfigurationSourceDirectory() throws InvalidConfigurationException {
		
		configuration.enableDebug();
		configuration.setStartupClass("TestWhile");
		configuration.addSourceDirectory("Hello Directory");
		configuration.addSourceDirectory("bin");
		
		Assert.assertEquals(
			"-main TestWhile -cp \"Hello Directory\" -cp bin -debug ", 
			configuration.printConfiguration());
	}
	
	/**
	 * Checks printing of the js configuration.
	 * Test method for {@link eclihx.core.haxe.internal.configuration.HaxeConfiguration#printConfiguration()}.
	 * @throws InvalidConfigurationException invalid setting of the configuration. 
	 */
	@Test
	public void testPrintConfigurationJS() throws InvalidConfigurationException {

		configuration.setStartupClass("TestJs");
		configuration.setPlatform(Platform.JavaScript);
		configuration.getJSConfig().setOutputFile("output.js");
		
		Assert.assertEquals(
			"-main TestJs -js output.js ", 
			configuration.printConfiguration());
		
	}
	
	/**
	 * Successful platform setting.
	 * 
	 * Test method for {@link eclihx.core.haxe.internal.configuration.HaxeConfiguration#setPlatform(eclihx.core.haxe.internal.configuration.HaxeConfiguration.Platform)}.
	 */
	@Test
	public void testSetPlatformSuccessful() {
		
		configuration.setExplicitNoOutput();
		configuration.setPlatform(Platform.Flash);
		configuration.setExplicitNoOutput();
		
	}
	
	/**
	 * Test method for {@link eclihx.core.haxe.internal.configuration.AbstractConfiguration#validate()}.
	 */
	@Test
	public void testValidate() {
		// TODO 9 Finish this method
		Assert.assertTrue(configuration.validate());
	}

}