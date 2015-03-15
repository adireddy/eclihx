package eclihx.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import eclihx.launching.flash.FlashDebugRunner;
import eclihx.launching.haxe.HaxeRunner;

/**
 * Start point for Haxe project launching.
 * See {@link LaunchConfigurationDelegate} and org.eclipse.debug.core.launchConfigurationTypes extension point.
 */
public class HaxeLaunchDelegate extends LaunchConfigurationDelegate{
	
	/**
	 * Finish launching information for user callback. 
	 */
	public class FinishLaunchInfo {
		private final String projectName;
		private final String output;
		private final String buildFileName;
		private final Boolean isSuccessful;
		
		/**
		 * Get the name of the project that was launched.
		 * @return the name of the project that was launched. 
		 */
		public String getProjectName() {
			return projectName;
		}
		/**
		 * Get the output string.
		 * @return the output string which should be showed to user.
		 */
		public String getOutput() {
			return output;
		}
		
		/**
		 * Get file with the build configuration.
		 * @return build file
		 */
		public String getBuildFile() {
			return buildFileName;
		}
		
		/**
		 * Was launching successful.
		 * @return launch success
		 */
		public Boolean isSuccess() {
			return isSuccessful;
		}
		
		/**
		 * Default constructor.
		 * @param isSuccess was launching successful
		 * @param output the string of the output.
		 * @param projectName the name of the project was launched.
		 * @param buildFileName build file name
		 */
		public FinishLaunchInfo(Boolean isSuccess, String output, String projectName, String buildFileName) {
			this.isSuccessful = isSuccess;
			this.output = output;
			this.projectName = projectName;
			this.buildFileName = buildFileName;
		}
	}
	
	/**
	 * Method chooses runner for current mode and configuration.
	 * @param mode
	 * @return
	 */
	private IHaxeRunner chooseHaxeRunner(String mode, HaxeRunnerConfiguration configuration) {
		
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
		
			// TODO 9 Check that this is a flash runner
			return new FlashDebugRunner();
			
		} else if (ILaunchManager.RUN_MODE.equals(mode)) {			
			return new HaxeRunner();			
		}
		
		return null;
	}
	
	/**
	 * Method sends finish notification to UI about the finishing of launching.
	 * @param isSuccess launch success. 
	 * @param projectName the name of project.
	 * @param output the output string.
	 * @param string build file path. 
	 * @throws CoreException
	 */
	private void sendFinishNotification(Boolean isSuccess, String projectName, String output, String buildFile) 
			throws CoreException {
        
		IStatus status = new Status(IStatus.ERROR, EclihxLauncher.PLUGIN_ID, 112, "", null); 
        IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);

        if (handler != null) {
        	handler.handleStatus(status, new FinishLaunchInfo(isSuccess, output, projectName, buildFile));
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public synchronized void launch(
			ILaunchConfiguration configuration, 
			String mode, 
			ILaunch launch, 
			IProgressMonitor monitor) throws CoreException {
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		monitor.beginTask("Launching...", 1);
		
		try {
			HaxeRunnerConfiguration haxeRunnerConfiguration = HaxeRunnerConfiguration.wrap(configuration);
			IHaxeRunner runner = chooseHaxeRunner(mode, haxeRunnerConfiguration);			
			
			String output = 
					runner.run(haxeRunnerConfiguration, launch, monitor);
			
			String projectName = configuration.getAttribute(
					IHaxeLaunchConfigurationConstants.PROJECT_NAME, (String) null);
			String buildFilePath = configuration.getAttribute(
					IHaxeLaunchConfigurationConstants.BUILD_FILE, (String) null); 
			
			// TODO 4: Change IHaxeRunner for sending information about build success
			sendFinishNotification(true, projectName, output, buildFilePath);
          
        } catch (CoreException e) {
        
        	throw e;
        
        } finally {
            monitor.done();        	
        }
	}	
}
