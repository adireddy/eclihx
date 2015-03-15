package eclihx.core.haxe.model;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFile;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;

/**
 * Haxe source folder class. This is the basic wrapper for the IFolder.
 */
public final class HaxeSourceFolder extends HaxeElement
		implements IHaxeSourceFolder {

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
	public HaxeSourceFolder(IHaxeProject project, IFolder folder) {
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
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#getBase()
	 */
	@Override
	public IFolder getBaseFolder() {
		return fFolder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getBaseResource()
	 */
	@Override
	public IResource getBaseResource() {
		return getBaseFolder();
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#getHaxeProject()
	 */
	@Override
	public IHaxeProject getHaxeProject() {
		return fHaxeProject;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#hasPackage(java.lang.String)
	 */
	@Override
	public boolean hasPackage(String packageName) {
		
		if (packageName.isEmpty()) {
			// This is the default package and it always exists.
			return true;
		}
		
		if (HaxeElementValidator.validatePackageName(packageName).isOK()) {
			
			String packagePath = packageName.replaceAll("\\.", "\\\\"); 
			
			return fFolder.exists(new Path(packagePath));
		}

		// If package name is invalid
		return false;
	}
	
	/**
	 * Creates new package with the given name.
	 * @param packageName the name of the package.
	 * @param monitor monitor for the operation. <code>null</code> 
	 *        value is allowed.
	 * @return created package.
	 * 
	 * @throws CoreException if there are some errors during folders creation.
	 * 
	 */
	@Override
	public IHaxePackage createPackage(String packageName, IProgressMonitor monitor) throws CoreException {
		String[] paths = packageName.split("\\.");
		
		IContainer container = fFolder;
		
		for (String folderPath : paths)
		{
			IFolder folder = container.getFolder(new Path(folderPath));
			
			if(!folder.exists()) {
				folder.create(true, true, monitor);
			}
			
			container = folder;
		}
		
		return getPackage(container.getFolder(new Path("")));
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getName()
	 */
	@Override
	public String getName() {
		return fFolder.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#getPackages()
	 */
	@Override
	public IHaxePackage[] getPackages() {
		try {
			ArrayList<IHaxePackage> haxePackages = 
				new ArrayList<IHaxePackage>();
			
			// Add a default package.
			haxePackages.add(new HaxePackage(this));
			
			IResource[] resources = fFolder.members();
			
			for (IResource resource : resources) {
				if (resource.getType() == IResource.FOLDER) {
					if (HaxeElementValidator.validatePackageName(resource.getName()).isOK()) {
						IHaxePackage curPackage = new HaxePackage(this, (IFolder)resource);
						haxePackages.add(curPackage);
						haxePackages.addAll(Arrays.asList(curPackage.getChildrenPackages()));
					}
				}
			}
			
			return haxePackages.toArray(new IHaxePackage[0]);
			
		} catch (CoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}
		
		return new IHaxePackage[0];
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#getPackage(org.eclipse.core.resources.IFolder)
	 */
	@Override
	public IHaxePackage getPackage(IFolder folder) {

		// Special case for default package
		if (this.fFolder.equals(folder)) {
			return new HaxePackage(this);
		}
		
		// Find the relative path to the source folder
		int matchingSegments = 
				folder.getFullPath().matchingFirstSegments(
						fFolder.getFullPath());
		
		IPath relativePath = 
			folder.getFullPath().removeFirstSegments(matchingSegments);
		
		if (!relativePath.isEmpty()) {
			
			String packageName = 
					relativePath.toString().replaceAll("/", "\\.");
			
			IHaxePackage[] packages = getPackages();
			
			for (IHaxePackage haxePackage : packages) {
				if (haxePackage.getName().equals(packageName)) {
					return haxePackage;
				}					
			}
		}		
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFolder#getSourceFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IHaxeSourceFile getSourceFile(IFile file) {
		if (HaxeElementValidator.validateHaxeFileName(file.getName()).isOK()) {
			
			IHaxePackage haxePackage = getPackage((IFolder)file.getParent());							
			
			if (haxePackage != null) {
				return new HaxeSourceFile(file, haxePackage);
			}
			
		}
		return null;
	}

}
