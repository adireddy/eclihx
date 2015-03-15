package eclihx.debug.flash;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;

import eclihx.core.haxe.internal.configuration.FlashConfiguration;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.InvalidConfigurationException;
import eclihx.core.haxe.internal.parser.BuildParamParser;
import eclihx.core.util.console.parser.core.ParseError;
import flash.tools.debugger.Bootstrap;
import flash.tools.debugger.Session;
import flash.tools.debugger.SessionManager;
import flash.tools.debugger.VersionException;


public class FlashRunner {
	
	private CoreException generateError(String message, Throwable exception) {
		return new CoreException(new Status(IStatus.OK, "debug", message, exception));
	}
	
	public FlashRunner() {
		// TODO Auto-generated constructor stub
	}

	public void run(ILaunch launch, String fileName, String workingDirectory) 
			throws CoreException {
		try {
			
			// TODO 2: Get output folder from the Haxe project
			String outputFolder = ""; 
			
			BuildParamParser parser = new BuildParamParser();			
			
			HaxeConfiguration config;
			try {
				config = parser.parseFile(fileName, workingDirectory).getMainConfiguration();
			} catch (InvalidConfigurationException e) {
				// TODO 6 Bad thing. We can fall here 
				return;
			} catch (ParseError e) {
				// TODO 7 We shouldn't fall. There should some user friendly message exist
				return;
			}
			FlashConfiguration flashConfig = config.getFlashConfig();
			
			
			if (!(flashConfig != null && config.isDebug() && config.hasCompilationFlags("fdb"))) return;
			
			String uriPath = outputFolder + "/" + flashConfig.getOutputFile();

			SessionManager flashManager = Bootstrap.sessionManager();

			if (flashManager.supportsLaunch()) {

				flashManager.startListening();
				Session debugSession = flashManager.launch(uriPath, null, true, null);
				//flashManager.accept(null);
				
				IProcess debugProcess = DebugPlugin.newProcess(launch,
						debugSession.getLaunchProcess(),
						"Haxe-Flash debug process");

				if (debugSession != null) {
					IDebugTarget target = new FlashDebugTarget(uriPath, launch,
							debugSession, debugProcess);
					launch.addDebugTarget(target);
				} else {					
				}
				
			} else {
				// TODO 8 the debugger will have to tell the user to manually launch the Flash player 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw generateError(e.toString(), e);
		} catch (VersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw generateError(e.toString(), e);
		}
	}

}
