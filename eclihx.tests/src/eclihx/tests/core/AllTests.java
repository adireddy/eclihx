package eclihx.tests.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	ASConfigurationTest.class, 
	BuildParamParserTest.class,
	CodeFormatterTest.class,
	FlashConfigurationTest.class,
	HaxeConfigurationTest.class,
	HaxeElementValidatorTest.class,
	// HaxeLauncherTest.class,
	JSConfigurationTest.class,
	NekoConfigurationTest.class,
	PHPConfigurationTest.class })
public class AllTests {
	
}
