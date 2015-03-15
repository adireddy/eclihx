package eclihx.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Class for managing configuration properties.
 */
public class HaxeRunnerConfiguration {
	
	private String[] arguments;
	private String workingDirectory;
	private String buildFile;
	private String compilerPath;
	private String projectName;
	
	/**
	 * Simple constructor. Main functionality moved to <code>load</code> method 
	 * because we don't want out constructor pelt us with exceptions.
	 */
	public HaxeRunnerConfiguration() {}
	
	/**
	 * Method performs loading configuration from the untyped 
	 * {@link ILaunchConfiguration} to this configuration and performs initial
	 * validation and conversion.
	 * 
	 * @param configuration the configuration to load.
	 * @throws CoreException for the reasons see the 
	 * {@link ILaunchConfiguration#getAttribute(String, String)} throw block.
	 */
	public void load(ILaunchConfiguration configuration) throws CoreException {

		setCompilerPath(configuration.getAttribute( 
				IHaxeLaunchConfigurationConstants.HAXE_COMPILER_PATH, ""));
        
		setBuildFile(configuration.getAttribute(
				IHaxeLaunchConfigurationConstants.BUILD_FILE, 
				(String) null));
		
		setWorkingDirectory(configuration.getAttribute(
				IHaxeLaunchConfigurationConstants.WORKING_DIRECTORY, 
				(String) null));
		
		setProjectName(configuration.getAttribute(
				IHaxeLaunchConfigurationConstants.PROJECT_NAME, 
				(String) null));
		
        // TODO 8 get attributes
        setArguments(null);
	}

	/**
	 * Get arguments.
	 * @return an array with arguments.
	 */
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * Set arguments.
	 * @param arguments array with arguments.
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * The working directory string.
	 * @return The working directory string. 
	 */
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Set the working directory
	 * @param workingDirectory the 
	 */
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/**
	 * Get the build file.
	 * @return the build file string.
	 */
	public String getBuildFile() {
		return buildFile;
	}

	/**
	 * Set the build file.
	 * @param buildFile the build file string.
	 */
	public void setBuildFile(String buildFile) {
		this.buildFile = buildFile;
	}

	/**
	 * Get the path of the compiler.
	 * @return the compiler path.
	 */
	public String getCompilerPath() {
		return compilerPath;
	}

	/**
	 * Get the path of the compiler.
	 * @param compilePath the compiler path.
	 */
	public void setCompilerPath(String compilePath) {
		compilerPath = compilePath;
	}
	
	/**
	 * Get the project name.
	 * @return the project name.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Set the project name.
	 * @param projectName project name.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
