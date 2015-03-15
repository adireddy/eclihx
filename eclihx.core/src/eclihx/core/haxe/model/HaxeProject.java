package eclihx.core.haxe.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.model.core.IHaxeBuildFile;
import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.core.haxe.model.core.IProjectPathManager;

/**
 * Extend eclipse project with Haxe functionality.
 */
public final class HaxeProject extends HaxeElement implements IHaxeProject {

	/**
	 * Original project
	 */
	IProject fProject;
	
	/**
	 * For to store project paths
	 */
	IProjectPathManager fPathManager;

	/**
	 * Create the Haxe project on the base of the IProject.
	 * This constructor will add a Haxe nature to the project.
	 *
	 * @param project the project which should become a Haxe project. It should
	 *        be opened.
	 */
	public HaxeProject(IProject project) {
		
		super(EclihxCore.getDefault().getHaxeWorkspace());
		
		fProject = project;
		
		if (!fProject.isOpen()) {
			throw new RuntimeException("Project should be opened");
		}
		
		try {
			addHaxeNature(); // Say to eclipse, that it's a Haxe project
		} catch (CoreException e) {
			// It should never happen.
			EclihxCore.getLogHelper().logError(e);
		} 			
	}
	
	/**
	 * Stores project.
	 */
	public void store() {
		// TODO 7 Make properties change listener
		if (fPathManager != null) {
			fPathManager.store();
		}
		
		try {
			new ProjectScope(fProject).getNode(EclihxCore.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}
	}
	
	/**
	 * Returns true if current project has Haxe nature.
	 * 
	 * @param project the project for check.
	 * @return true if current project has Haxe nature.
	 */
	public static boolean isHaxeProject(IProject project) {
		try {
			return project.isOpen() && project.hasNature(HAXE_PROJECT_NATURE_ID);
		} catch (CoreException e) {
			// Project doesn't exist or isn't opened.
			// In fact we should never get here, because we have checked 
			// isOpen first. Catch block was added to prevent 
			// appearing exception in the method signature.
			return false;
		}
	}
	

	/**
	 * Recursive search for build files 
	 * @param resource Root of current search
	 * @return List of build files
	 * @throws CoreException
	 */
	private ArrayList<IFile> getBuildFiles(IResource resource) throws CoreException
	{
		// TODO 2 Find a better algorithm for this method. Maybe eclipse provide standard 
		// for such work
		if (resource instanceof IContainer) {
			IContainer containerResource = (IContainer) resource;
			IResource[] resources = containerResource.members();
			
			ArrayList<IFile> buildFiles = new ArrayList<IFile>();
			
			for(IResource memberResource : resources) {
				if (memberResource.getType() == IResource.FILE) {
					//TODO 4 replace "hxml" with a constant (good practice)
					if ("hxml".equals(memberResource.getFileExtension())) {
						buildFiles.add((IFile)memberResource);
					}			
				} else {
					buildFiles.addAll(getBuildFiles(memberResource));
				}					
			}
			
			return buildFiles;			
		}
		
		// In fact we should never reach this code, because we don't call method for files.
		return new ArrayList<IFile>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#hasBuildFile(java.lang.String)
	 */
	public boolean hasBuildFile(String fileName) {
		return fProject.getFile(fileName).exists();
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#createBuildFile(java.lang.String)
	 */
	@Override
	public IHaxeBuildFile createBuildFile(
			String fileName, IProgressMonitor monitor) throws CoreException {
		
		return createBuildFile(fileName, "", monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#createBuildFile(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IHaxeBuildFile createBuildFile(String fileName,
			String initialContent, IProgressMonitor monitor)
			throws CoreException {
		
		if (!HaxeElementValidator.validateBuildFileName(fileName).isOK()) {
			throw new CoreException(new Status(
					IStatus.ERROR, EclihxCore.PLUGIN_ID,
					"Build file name is invalid"));
		}		
		
		InputStream stream = new ByteArrayInputStream((initialContent).getBytes());
				
		IFile buildFile = fProject.getFile(fileName);
		buildFile.create(stream, true, monitor);
		
		HaxeBuildFile haxeBuildFile = new HaxeBuildFile(this, buildFile);					
		return haxeBuildFile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getBuildFiles()
	 */
	@Override
	public IFile[] getBuildFiles() throws CoreException {
		return getBuildFiles(fProject).toArray(new IFile[0]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getBuildFilesElements()
	 */
	@Override
	public IHaxeBuildFile[] getBuildFilesElements() throws CoreException {
		ArrayList<IHaxeBuildFile> wrappedFiles = 
				new ArrayList<IHaxeBuildFile>();
		ArrayList<IFile> buildFiles = getBuildFiles(fProject);
		
		for (IFile file : buildFiles) {
			wrappedFiles.add(new HaxeBuildFile(this, file));
		}
		
		return wrappedFiles.toArray(new IHaxeBuildFile[0]);
	}
	
	/* (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getPathManager()
	 */
	public IProjectPathManager getPathManager() {
		if (fPathManager == null) {
			fPathManager = ProjectPathManager.create(this);
			return fPathManager;
		}
		
		return fPathManager;
	}

	/* (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getProjectBase()
	 */
	@Override
	public IProject getProjectBase() {
		return fProject;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getBaseResource()
	 */
	@Override
	public IResource getBaseResource() {
		return getProjectBase();
	}
	
	
	/* (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#isOpen()
	 */
	public boolean isOpen() {
		return fProject.isOpen();
	}
	
	/* (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#open(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void open(IProgressMonitor monitor) throws CoreException { 
		fProject.open(monitor);
		addHaxeNature(); // Say eclipse, that it's a Haxe project
	}
	
	/**
	 * Add Haxe nature to the project, if it hasn't been added before
     *
	 * @throws CoreException if project hasn't been opened
	 */
	protected void addHaxeNature() throws CoreException {
		addNature(HAXE_PROJECT_NATURE_ID);
	}
	
	/**
	 * Add nature to the project
	 * @param natureID
	 * @throws CoreException if base project hasn't been opened
	 */
	protected void addNature(String natureID) throws CoreException  {
		
		assert(fProject.isOpen()); // The project should be opened
		IProjectDescription description = fProject.getDescription();

		if (!description.hasNature(natureID)) {
			// if project doesn't have current nature yet

			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = natureID;
			description.setNatureIds(newNatures);

			fProject.setDescription(description, null);

		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getName()
	 */
	@Override
	public String getName() {
		return fProject.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getSourceFolders()
	 */
	@Override
	public IHaxeSourceFolder[] getSourceFolders() {
		ArrayList<IHaxeSourceFolder> sourceFolders = 
			new ArrayList<IHaxeSourceFolder>();
		
		for(IFolder folder : getPathManager().getSourceFolders()) {
			sourceFolders.add(new HaxeSourceFolder(this, folder));
		}
		
		return sourceFolders.toArray(new IHaxeSourceFolder[0]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getSourceFolder(org.eclipse.core.resources.IFolder)
	 */
	@Override
	public IHaxeSourceFolder getSourceFolder(IFolder folder) {
		for(IFolder sourceFolder : getPathManager().getSourceFolders()) {
			if (sourceFolder.equals(folder)) {
				return new HaxeSourceFolder(this, sourceFolder);
			}			
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#getOutputFolder()
	 */
	@Override
	public IHaxeOutputFolder getOutputFolder() {
		return new HaxeOutputFolder(this, getPathManager().getOutputFolder());
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#createOutputFolder(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFolder createOutputFolder(String outputFolderName,
			IProgressMonitor monitor) throws CoreException {
		
		if (!HaxeElementValidator.validateHaxeOutputFolderName(
				outputFolderName).isOK()) {
			
			throw new CoreException(new Status(
					IStatus.ERROR, EclihxCore.PLUGIN_ID,
					"Invalid source folder name"));
		}

		IFolder folder = fProject.getFolder(outputFolderName);
		
		folder.create(false, true, monitor);

		getPathManager().setOutputFolder(folder);
		
		//TODO 5 Move to caching projects info and remove this directive.
		getPathManager().store();
		
		return folder;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.model.core.IHaxeProject#createSourceFolder(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IHaxeSourceFolder createSourceFolder(String sourceFolderName,
			IProgressMonitor monitor) throws CoreException {
		
		if (!HaxeElementValidator.validateHaxeSourceFolderName(
				sourceFolderName).isOK()) {
			
			throw new CoreException(new Status(
					IStatus.ERROR, EclihxCore.PLUGIN_ID,
					"Invalid source folder name"));
		}
		
		IFolder folder = fProject.getFolder(sourceFolderName);
		
		folder.create(false, true, monitor);
		
		getPathManager().addSourceFolder(folder);
		
		//TODO 5 Move to caching projects info and remove this directive.
		getPathManager().store();

		return new HaxeSourceFolder(this, folder);
	}

	@Override
	public void setContentAssistBuildFile(String absolutePath) throws InvalidBuildFileNameException {
		IEclipsePreferences scope = new ProjectScope(fProject).getNode(EclihxCore.PLUGIN_ID);
		Preferences contentAssistNode = scope.node("contentassist");
		
		if (absolutePath == null || absolutePath.isEmpty()) {
			try {
				contentAssistNode.clear();
			} catch (BackingStoreException e) {
				EclihxCore.getLogHelper().logError(e);
			}
		} else {
			IPath path = new Path(absolutePath);

			if (!path.isAbsolute()) {
				throw new InvalidBuildFileNameException("Path should be absolute");
			}
			
			if (!fProject.getLocation().isPrefixOf(path)) {
				throw new InvalidBuildFileNameException("Should be relative to the project");
			}
			
			IPath thisProjectRelative = path.makeRelativeTo(fProject.getLocation());
			
			if (!fProject.getFile(thisProjectRelative).exists()) {
				throw new InvalidBuildFileNameException("File should exist");
			}			
			
			Assert.isTrue(path != thisProjectRelative);			
			contentAssistNode.put("buildfile", thisProjectRelative.toOSString());
		}		
	}

	@Override
	public String getContentAssistBuildFileAbsolute() {
		IEclipsePreferences scope = new ProjectScope(fProject).getNode(EclihxCore.PLUGIN_ID);
		Preferences contentAssistNode = scope.node("contentassist");
		String buildPath = contentAssistNode.get("buildfile", null);
		
		if (buildPath != null && !buildPath.isEmpty()) {
			return fProject.getFile(new Path(buildPath)).getLocation().toOSString();
		}
		
		return null;
	}
}
