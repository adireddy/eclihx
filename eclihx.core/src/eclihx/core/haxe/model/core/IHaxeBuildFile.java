package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IFile;

/**
 * A wrapper for the build file.
 */
public interface IHaxeBuildFile extends IHaxeElement {
	
	/**
	 * Returns the IFile resource for this build file.
	 * @return the IFile resource.
	 */
	IFile getBaseFile();
	
	/**
	 * Returns the Haxe project.
	 * @return the package of the file.
	 */
	IHaxeProject getProject();
	
}
