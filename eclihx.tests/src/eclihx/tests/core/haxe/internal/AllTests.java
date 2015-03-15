package eclihx.tests.core.haxe.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	eclihx.tests.core.haxe.internal.versioning.AllTests.class,
	HaxeContentManagerTest.class,
	HaxeOutputErrorsParserTest.class
} )
public class AllTests {

}
