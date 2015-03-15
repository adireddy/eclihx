package eclihx.core.haxe;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.InvalidConfigurationException;
import eclihx.core.util.OSUtil;
import eclihx.core.util.ProcessUtil;

/**
 * Class launches the Haxe process with the given configuration.
 */
public class HaxeLauncher {

	/**
	 * Errors of the execution.
	 */
	private String errorsString = "";

	/**
	 * Output string of the execution.
	 */
	private String outputString = "";

	/**
	 * Runs the given configuration.
	 * 
	 * @param configuration the configuration to run.
	 * @param launch launch object
	 * @param compilerPath the compiler path.
	 * @param workingDirectory output directory.
	 * @throws CoreException exception if there are some errors during
	 *         execution.
	 */
	public synchronized void run(HaxeConfiguration configuration,
			ILaunch launch, String compilerPath, File workingDirectory)
			throws CoreException {

		try {

			String commandLine = OSUtil.quoteCompoundPath(compilerPath) + " "
					+ configuration.printConfiguration();
			
			ProcessUtil.ProcessExecResult execResult = ProcessUtil.executeProcess(commandLine, workingDirectory);
			
			errorsString = execResult.getErrorsString();
			outputString = execResult.getOutputString();

		} catch (InvalidConfigurationException e) {
			throw new CoreException(
					new Status(
							Status.ERROR,
							EclihxCore.PLUGIN_ID, 
							"Invalid configuration for launch: "
							+ e.getMessage()));
		}
	}
	
	/**
	 * Runs the given configuration.
	 * 
	 * @param buildFilePath buildFilePath
	 * @param launch launch object
	 * @param compilerPath the compiler path.
	 * @param workingDirectory output directory.
	 */
	public synchronized void run(String buildFilePath,
			ILaunch launch, String compilerPath, File workingDirectory) {

		String commandLine = OSUtil.quoteCompoundPath(compilerPath) + " " + OSUtil.quoteCompoundPath(buildFilePath);
		ProcessUtil.ProcessExecResult execResult = ProcessUtil.executeProcess(commandLine, workingDirectory);

		errorsString = execResult.getErrorsString();
		outputString = execResult.getOutputString();
	}

	/**
	 * Get the errors of the execution.
	 * 
	 * @return the errors
	 */
	public synchronized String getErrorString() {
		return errorsString;
	}

	/**
	 * Get the output of the execution.
	 * 
	 * @return the output
	 */
	public synchronized String getOutputString() {
		return outputString;
	}

}
