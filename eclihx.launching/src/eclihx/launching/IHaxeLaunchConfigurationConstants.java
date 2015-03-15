package eclihx.launching;

/**
 * Specific constants for launching.
 */
public interface IHaxeLaunchConfigurationConstants {

	/**
	 * Haxe compiler path. 
	 */
	public static final String HAXE_COMPILER_PATH = 
			EclihxLauncher.PLUGIN_ID + ".haxe_compiler";
	
	/**
	 * Haxe compiler attributes
	 */
	public static final String ATTR_ARGUMENTS = 
			EclihxLauncher.PLUGIN_ID + ".haxe_arguments";
	
	/**
	 * hxml file which we are going to compile
	 */
	public static final String BUILD_FILE = 
			EclihxLauncher.PLUGIN_ID + ".build_file";
	
	/**
	 * The name of the associated project
	 */
	public static final String PROJECT_NAME = 
			EclihxLauncher.PLUGIN_ID + ".project_name";
	
	/**
	 * Directory where compiling should be performed
	 */
	public static final String WORKING_DIRECTORY = 
			EclihxLauncher.PLUGIN_ID + ".working_directory";
}
