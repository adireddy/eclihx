package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface for the workspace wrapper aimed to manage Haxe resources.
 */
public interface IHaxeWorkspace extends IHaxeElement {

	/**
	 * Closes the workspace and saves Haxe projects in workspace 
	 */
	void close();
	
	/**
	 * Gets the Haxe element wrapper for resource.
	 * @param resource the resource which is suspected to be Haxe element.
	 * @return IHaxeElement if resource is a Haxe resource and 
	 * <code>null</code> in other case.
	 */
	IHaxeElement getHaxeElement(IResource resource);
	
	/**
	 * Get the array with the all Haxe project names.
	 * @return an array with the Haxe project names.
	 */
	String[] getHaxeProjectsNames();

	/**
	 * Gets Haxe project by name.
	 * @param projectName the name of the project.
	 * 
	 * @return the reference to Haxe project or null if 
	 *         such project doesn't exist.
	 */
	IHaxeProject getHaxeProject(String projectName);
	
	/**
	 * Gets Haxe project by IProject object.
	 * @param project the project object.
	 * 
	 * @return the reference to Haxe project or null if 
	 *         such project doesn't exist.
	 */
	IHaxeProject getHaxeProject(IProject project);
	
	/**
	 * Creates the Haxe project.
	 * @param projectName the name of the project to create.
	 * @param monitor the operation monitor. <code>null</code> 
	 *        value is allowed.
	 *        
	 * @return the Haxe project.
	 *        
	 * @throws CoreException if there are some errors during project creation.
	 */
	IHaxeProject createHaxeProject(String projectName, IProgressMonitor monitor) 
			throws CoreException;
	
	/**
	 * Get all Haxe projects in the workspace.
	 * 
	 * @return An array of Haxe projects.
	 */
	IHaxeProject[] getHaxeProjects();

	

}