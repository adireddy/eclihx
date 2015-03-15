package eclihx.core;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import eclihx.core.haxe.model.HaxeWorkspace;
import eclihx.core.haxe.model.core.IHaxeWorkspace;

/**
 * The activator class controls the plug-in life cycle.
 */
public class EclihxCore extends Plugin {

	/**
	 * Wrapper for the original workspace to work with Haxe language.
	 */
	private IHaxeWorkspace haxeWorkspace;
	
	/**
	 * Logger helper.
	 */
	private static IPluginLogger logger;

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "eclihx.core";

	/**
	 * The shared instance of plug-in.
	 */
	private static EclihxCore plugin;

	/**
	 * The default constructor.
	 */
	public EclihxCore() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		haxeWorkspace = new HaxeWorkspace(ResourcesPlugin.getWorkspace()
				.getRoot());		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		
		savePluginPreferences();
		
		haxeWorkspace.close();

		plugin = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EclihxCore getDefault() {
		return plugin;
	}

	/**
	 * Getter of the property <tt>haxeWorkspace</tt>
	 * 
	 * @return Returns the haxeWorkspace.
	 * 
	 */
	public IHaxeWorkspace getHaxeWorkspace() {
		return haxeWorkspace;
	}
	
	/**
	 * Get plug-in log helper.  
	 * @return plug-in helper.
	 */
	public static IPluginLogger getLogHelper() {
		if (logger == null) {
			logger = new EclihxLogger(getDefault(), PLUGIN_ID);
		}		
		
		return logger;
	}
}
