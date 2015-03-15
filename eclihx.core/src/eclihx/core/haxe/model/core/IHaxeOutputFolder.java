package eclihx.core.haxe.model.core;

import org.eclipse.core.resources.IFolder;

/**
 * Interface for project output folder.
 * TODO 7: Synchronize with path manager. 
 */
public interface IHaxeOutputFolder extends IHaxeElement {
	
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

}
