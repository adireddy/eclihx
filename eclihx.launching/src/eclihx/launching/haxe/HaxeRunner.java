package eclihx.launching.haxe;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStatusHandler;

import eclihx.core.haxe.HaxeCompilerResolver;
import eclihx.core.haxe.HaxeLauncher;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.HaxeConfigurationList;
import eclihx.core.haxe.internal.parser.BuildParamParser;
import eclihx.core.util.OSUtil;
import eclihx.core.util.console.parser.core.ParseError;
import eclihx.launching.EclihxLauncher;
import eclihx.launching.HaxeRunnerConfiguration;
import eclihx.launching.IHaxeRunner;

/**
 * Class starts the Haxe compiler process. 
 */
public class HaxeRunner implements IHaxeRunner {
	
	private void throwState(int severity, int code, String message) 
			throws CoreException {
		
		IStatus status = new Status(
				severity, EclihxLauncher.PLUGIN_ID, code, message, null);
		
		IStatusHandler handler = 
				DebugPlugin.getDefault().getStatusHandler(status);
	
		if (handler == null) {
			// if there is no handler, throw the exception
			throw new CoreException(status);
		} else {
			Object result = handler.handleStatus(status, null);
			
			if (result instanceof Boolean) {
				boolean stop = ((Boolean)result).booleanValue();
				if (stop) {
					throw (new CoreException(status));
				}
			}
		}				
	}
	
	/**
	 * Check if we have valid configuration for launching.
	 * 
	 * @param config
	 * @throws CoreException
	 */
	private void validateConfiguration(HaxeRunnerConfiguration config) 
			throws CoreException {
		
		IStatus status = OSUtil.validateCompilerPath(getLaunchCompilerPath(config));
		if (!status.isOK()) {
			throwState(IStatus.ERROR, IStatus.ERROR, status.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.launching.IHaxeRunner#run(eclihx.launching.HaxeRunnerConfiguration, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String run(HaxeRunnerConfiguration configuration, 
					ILaunch launch, 
					IProgressMonitor monitor) throws CoreException {
		
		validateConfiguration(configuration);
		
        File workingDirectory = new File(configuration.getWorkingDirectory());
        
        HaxeConfigurationList executionList;
        
        try {
        	BuildParamParser parser = new BuildParamParser();
        	
        	executionList = 
        		parser.parseFile(configuration.getBuildFile(), configuration.getWorkingDirectory());
        } catch (ParseError e) {
			return e.getMessage();
		}
        
        for (HaxeConfiguration haxeConfig : executionList) {
        	ArrayList<String> configErrors = haxeConfig.validate();
        	if (!configErrors.isEmpty()) {
        		return configErrors.toString();
        	}
        }
        
        final HaxeLauncher launcher = new HaxeLauncher();
        
        launcher.run(configuration.getBuildFile(), launch, getLaunchCompilerPath(configuration), workingDirectory);
        
        if (!launcher.getErrorString().isEmpty()) {
        	return launcher.getErrorString();
        }        
        
        return "Building complete.\n" + launcher.getOutputString();
	}
	
	/**
	 * Get the compiler path for given configuration.
	 * @param configuration configuration.
	 * @return a special or default compiler path.
	 */
	public static String getLaunchCompilerPath(HaxeRunnerConfiguration configuration) {
		// TODO 5: Implement project scope compiler setting
        return configuration.isNonDefaultCompiler() ? 
        		configuration.getCompilerPath() :
        		HaxeCompilerResolver.getDefaultGlobalCompiler();
	}
}
