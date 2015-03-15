package eclihx.core.haxe.model;

import java.security.InvalidParameterException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxeProject;

/**
 * Wrapper of IFolder for working with Haxe.
 */
public class HaxeOutputFolder extends HaxeElement implements IHaxeOutputFolder {
	
	/**
	 * Base folder object.
	 */
	private final IFolder fFolder;
	
	/**
	 * Owner Haxe project. 
	 */
	private final IHaxeProject fHaxeProject;
	
	/**
	 * Default constructor.
	 * @param project the Haxe owner project.
	 * @param folder the base IFolder object.
	 */
	public HaxeOutputFolder(IHaxeProject project, IFolder folder) {
		super(project);
		
		if (!folder.getProject().equals(project.getProjectBase())) {
			throw new InvalidParameterException(
					"Folder parameter should be direct sub-folder of the " +
					"given project.");
		}				
		
		fFolder = folder;
		fHaxeProject = project;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeOutputFolder#getBaseFolder()
	 */
	@Override
	public IFolder getBaseFolder() {
		return fFolder;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeOutputFolder#getHaxeProject()
	 */
	@Override
	public IHaxeProject getHaxeProject() {
		return fHaxeProject;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getBaseResource()
	 */
	@Override
	public IResource getBaseResource() {
		return fFolder;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getName()
	 */
	@Override
	public String getName() {
		return fFolder.getName();
	}

}
