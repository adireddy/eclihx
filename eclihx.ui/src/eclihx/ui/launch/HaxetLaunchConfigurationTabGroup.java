package eclihx.ui.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import eclihx.ui.launch.tabs.HaxeMainTab;

/**
 * Defines tabs for Haxe launch configuration wizard.
 */
public class HaxetLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {
	
    /*
     * @see ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog, String)
     */
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = {
        		new HaxeMainTab(),
            };
        
        setTabs(tabs);
    }
	
}
