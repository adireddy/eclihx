package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IFile;

/**
 * Interface for the haXe project.
 */
public interface IHaxeSourceFile extends IHaxeElement {
	
	/**
	 * Returns the IFile resource for this haXe source file
	 * @return the IFile resource.
	 */
	IFile getBaseFile();
	
	/**
	 * Returns the package of the file.
	 * @return the package of the file.
	 */
	IHaxePackage getPackage();
}
