package eclihx.core.haxe;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.versioning.HaxeVersion;

/**
 * Utilities methods for working with Haxe compiler.
 */
public class HaxeCompilerExecution {
	
	/**
	 * Exception is thrown if eclihx failed in determining haxe compiler version.
	 */
	@SuppressWarnings("serial")
	public static class UnknownVersionException extends Exception {
		UnknownVersionException() { super(); }
		UnknownVersionException(String message, Throwable cause) { super(message, cause); }
		UnknownVersionException(String message) { super(message); }
		UnknownVersionException(Throwable cause) { super(cause); }
	}
	
	/**
	 * Get compiler version.
	 * @param compilerPath compiler executable.
	 * @return version.
	 * @throws UnknownVersionException if request failed for some reason.
	 */
	public static HaxeVersion getVersion(String compilerPath) throws UnknownVersionException {
		try {
			HaxeLauncher launcher = new HaxeLauncher();
			HaxeConfiguration haxeConfiguration = new HaxeConfiguration();
			haxeConfiguration.enableHelp();
			
			launcher.run(haxeConfiguration, null, compilerPath, new File(System.getProperty("java.io.tmpdir")));
			
			String outputString = launcher.getOutputString();
			
			String other = outputString;
			
			Pattern pattern = Pattern.compile(".*Compiler\\s([^\\s]*).*", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(outputString);
			if (!matcher.matches()) {
				throw new UnknownVersionException("Can't find compiler string");
			}
			
			String version = matcher.group(1);
			return new HaxeVersion(version);
		} catch (Exception e) {
			throw new UnknownVersionException(e);
		}
	}
}
