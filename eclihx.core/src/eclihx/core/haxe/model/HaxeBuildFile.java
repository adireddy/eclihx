package eclihx.core.haxe.model;

import java.security.InvalidParameterException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import eclihx.core.haxe.model.core.IHaxeBuildFile;
import eclihx.core.haxe.model.core.IHaxeProject;

/**
 * Wrapper of the IFile resource for to make build file abstraction.
 */
public class HaxeBuildFile extends HaxeElement implements IHaxeBuildFile {

	/**
	 * File resource for this object.
	 */
	private final IFile fFile;
	
	/**
	 * Haxe project of this file.
	 */
	private final IHaxeProject fHaxeProject; 
	
	/**
	 * Standard constructor.
	 * @param project the project of build file.
	 * @param file the base resource object.
	 */
	public HaxeBuildFile(IHaxeProject project, IFile file) {
		super(project);
		
		if (!file.getProject().equals(project.getProjectBase())) {
			throw new InvalidParameterException(
					"File should belong to the given project.");
		}				
		
		fFile = file;
		fHaxeProject = project;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeBuildFile#getBaseFile()
	 */
	@Override
	public IFile getBaseFile() {
		return fFile;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeBuildFile#getProject()
	 */
	@Override
	public IHaxeProject getProject() {
		return fHaxeProject;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getBaseResource()
	 */
	@Override
	public IResource getBaseResource() {
		return fFile;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getName()
	 */
	@Override
	public String getName() {
		return fFile.getName();
	}

}
