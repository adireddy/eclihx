package eclihx.core.haxe.model.core;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.model.InvalidBuildFileNameException;

/**
 * Interface for the Haxe project.
 */
public interface IHaxeProject extends IHaxeElement {
	
	/**
	 * Haxe project nature.
	 */
	String HAXE_PROJECT_NATURE_ID = "eclihx.core.haxenature";
	
	/**
	 * Default source folder name.
	 */
	String DEFAULT_SOURCE_FOLDER_NAME = "src";
	
	/**
	 * Default source folder name.
	 */
	String DEFAULT_BUILD_FILE_NAME = "build.hxml";
	
	/**
	 * Default source folder name.
	 */
	String DEFAULT_OUTPUT_FOLDER_NAME = "out";
	
	/**
	 * Return the base object
	 * @return the base IProject object.
	 */
	IProject getProjectBase();
	
	/**
	 * Gets path manager. It can be used to find out info about folders, 
	 * like Source, Binary, Library
	 *  
	 * @return Project path manager
	 * 
	 * @deprecated
	 */
	@Deprecated
	IProjectPathManager getPathManager();
	
	
	/**
	 * Get the project output folder.
	 * @return The output folder of the project.
	 * 
	 */
	IHaxeOutputFolder getOutputFolder();
	
	
	/**
	 * Gets list of build files paths
	 * @return array of IFile interfaces with build files
	 * @throws CoreException 
	 */
	IFile[] getBuildFiles() throws CoreException;
	
	/**
	 * Get the list of wrapped Haxe build files.
	 * @return an array of build files wrappers.
	 * @throws CoreException
	 */
	IHaxeBuildFile[] getBuildFilesElements() throws CoreException;
	
	/**
	 * Examines the project for build file existence.
	 * 
	 * @param fileName the name of the build file.
	 *  
	 * @return <code>true</code> if this project has already build file with
	 *         such name. 
	 */
	boolean hasBuildFile(String fileName);
	
	/**
	 * Creates new build file.
	 * 
	 * @param fileName the name of the build file.
	 * @param monitor the operation monitor. <code>null</code> 
	 *        value is allowed.
	 * @return the created build file.
	 * @throws CoreException if file name is not a build file name, or
	 * 		   the error of IFile.create method.
	 */
	IHaxeBuildFile createBuildFile(String fileName, IProgressMonitor monitor) 
			throws CoreException;
	
	/**
	 * Creates new build file.
	 * 
	 * @param fileName the name of the build file.
	 * @param initialContent Initial file content.
	 * @param monitor the operation monitor. <code>null</code> 
	 *        value is allowed.
	 * @return the created build file.
	 * @throws CoreException if file name is not a build file name, or
	 * 		   the error of IFile.create method.
	 */
	IHaxeBuildFile createBuildFile(String fileName, String initialContent, IProgressMonitor monitor) 
			throws CoreException;	
	
	/**
	 * Creates a new source folder.
	 * 
	 * @param sourceFolderName the name of the source folder.
	 * @param monitor the operation monitor. <code>null</code> 
	 *        value is allowed.
	 * 
	 * @return the created source folder. 
	 * 
	 * @throws CoreException if there are some errors during folder creation.
	 */
	IHaxeSourceFolder createSourceFolder(
			String sourceFolderName, IProgressMonitor monitor) 
			throws CoreException;
	
	/**
	 * Creates new output folder if it is doesn't exist.
	 *
	 * @param outputFolderName the name of the output folder.
	 * @param monitor the operation monitor. <code>null</code> 
	 *        value is allowed.
	 *        
	 * @return the output folder.
	 *        
	 * @throws CoreException if there are some errors during file creation.
	 */
	IFolder createOutputFolder(
			String outputFolderName, IProgressMonitor monitor) 
			throws CoreException;
	
	/**
	 * Returns the source folder of the project.
	 * @return an array of the source folders.
	 */
	IHaxeSourceFolder[] getSourceFolders();
	
	/**
	 * Get the source folder by the wrapped resource.
	 * @param folder the original resource.
	 * @return the Haxe source folder or <code>null<> if there are no source
	 * folder with this resource.
	 */
	IHaxeSourceFolder getSourceFolder(IFolder folder);	
	
	
	/**
	 * Checks if project is opened.
	 * 
	 * @return <code>true</code> if project is open.
	 */
	boolean isOpen();
	
	/**
	 * Opens base project and do some advanced work for the Haxe project
	 * @param monitor
	 * @throws CoreException
	 */
	void open(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Get the name of the project.
	 * @return the project name.
	 */
	String getName();
	
	/**
	 * Set the content assist absolute path.
	 * @param absolutePath New content assist absolute path. Expected to be in current project and exists.
	 * 		If null or empty the property will be unset. 
	 * @throws InvalidBuildFileNameException If file not absolute, not relative to the project or doesn't exists.
	 */
	void setContentAssistBuildFile(String absolutePath) throws InvalidBuildFileNameException;
	
	/**
	 * Get the absolute build file path that should be used for content assist.
	 * @return Absolute build file path should be used for content assist. Could be null if file wasn't set.
	 */
	String getContentAssistBuildFileAbsolute();
}
