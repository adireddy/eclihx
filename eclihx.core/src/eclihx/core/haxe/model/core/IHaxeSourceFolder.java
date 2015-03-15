package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A wrapper for the Haxe source folder.
 */
public interface IHaxeSourceFolder extends IHaxeElement {
	
	/**
	 * Get the Haxe project this folder belongs to.
	 * @return the Haxe project.
	 */
	IHaxeProject getHaxeProject();
	
	/**
	 * Return the base object
	 * @return the base IFolder object.
	 */
	IFolder getBaseFolder();
	
	/**
	 * Method checks if this source folder already has the package.
	 * @param packageName the package name.
	 * @return <code>true</code> if source folder contains the specified
	 *         package.
	 */
	boolean hasPackage(String packageName);
	
	/**
	 * Creates new package with the given name.
	 * @param packageName the name of the package.
	 * @param monitor monitor for the operation. <code>null</code> 
	 *        value is allowed.
	 * 
	 * @throws CoreException if there are some errors during folders creation.
	 */
	void createPackage(String packageName, IProgressMonitor monitor) 
			throws CoreException;
	
	/**
	 * Get the array of the all Haxe packages in the folder.
	 * @return the array with the packages.
	 */
	IHaxePackage[] getPackages();
	
	/**
	 * Get the package wrapped with this folder.
	 * @param folder the folder resource.
	 * @return the package element or <code>null</code> if there are 
	 * no such package. 
	 */
	IHaxePackage getPackage(IFolder folder);
	
	/**
	 * Get the wrapped source file.
	 * @param file the given file resource.
	 * @return the element or <code>null</code> if there are 
	 * no such source file. 
	 */
	IHaxeSourceFile getSourceFile(IFile file);
}
