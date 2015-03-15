package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface for the Haxe package element.
 */
public interface IHaxePackage extends IHaxeElement {

	/**
	 * The name for the default package.
	 */
	String DEFAULT_PACKAGE_NAME = "(Default Package)";

	/**
	 * Return the base object.
	 * 
	 * @return the base IFolder object.
	 */
	IFolder getBaseFolder();

	/**
	 * Get source folder.
	 * 
	 * @return the Haxe source folder this package is situated in.
	 */
	IHaxeSourceFolder getSourceFolder();
	
	/**
	 * Returns all children packages at all depth.
	 * 
	 * @return an array of children packages.
	 */
	IHaxePackage[] getChildrenPackages();

	/**
	 * Checks if file with the given filename exists in the package.
	 * 
	 * @param haxeFileName the name of the Haxe file.
	 * @return <code>true</code> if file exists in the folder.
	 *         <code>false</code> in both cases file is not a Haxe file or it
	 *         doesn't exist in the package.
	 */
	boolean hasHaxeFile(String haxeFileName);

	/**
	 * Create a Haxe file in the package.
	 * 
	 * @param haxeFileName the name of the file to create.
	 * @param monitor the operation monitor. <code>null</code> value is allowed.
	 * @return newly created Haxe source file.
	 * 
	 * @throws CoreException if there are some errors during file creation.
	 */
	public IHaxeSourceFile createHaxeFile(
			String haxeFileName, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Get the source files in this package.
	 * 
	 * @return an array of Haxe source files.
	 */
	IHaxeSourceFile[] getHaxeSourceFiles();
	
	/**
	 * Get the source files in this package in IFile format
	 * 
	 * @return an array of source files.
	 */
	IFile[] getSourceFiles();
	
	/**
	 * Checks if the package is default.
	 * @return <code>true</code> for default package.
	 */
	boolean isDefault();
	
	/**
	 * Check if the package has child source files.
	 * 
	 * @return <code>true</code> if package is empty. 
	 */
	boolean isEmpty();

}
