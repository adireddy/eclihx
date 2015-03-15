package eclihx.core.haxe.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IProjectPathManager;

/**
 * Class for storing paths of the project
 */
public class ProjectPathManager implements IProjectPathManager {
	
	private static final String fileName = ".paths";

	/**
	 * Project of this manager.
	 */
	private final IHaxeProject fProject;
	
	/**
	 * Project output folder. 
	 */
	private IFolder outputFolder;
	
	/**
	 * A list with source folders.
	 */
	private List<IFolder> sourceFolders = new LinkedList<IFolder>();
	
	/**
	 * Folders with libraries.
	 */
	private final List<IFolder> libFolders = new LinkedList<IFolder>();
	
	/**
	 * Special files of libraries.
	 */
	private final List<IFile> libFiles = new LinkedList<IFile>();
	
	
	/**
	 * Default constructor.
	 * @param project the Haxe project this manager is belong to.
	 */
	private ProjectPathManager(IHaxeProject project) {
		fProject = project;
	}
	
	/**
	 * Creating manager for the project and loading with values stored in the
	 * second parameter.
	 *  
	 * @param project project the Haxe project this manager is belong to.
	 * @param pathsStore a storage with the paths.
	 */
	private ProjectPathManager(IHaxeProject project, 
			ProjectPathsSerializer pathsStore) {

		this(project);
		load(pathsStore);
	}

	/**
	 * Loading values from the given store.
	 *
	 * @param pathsStore a storage with the paths.
	 */
	private void load(ProjectPathsSerializer pathsStore) {
		outputFolder = fProject.getProjectBase().getFolder(
				pathsStore.outputFolder);
		
		sourceFolders.clear();
		if (pathsStore.sourceFolders != null) {
			for (String sourceFolder : pathsStore.sourceFolders) {
				sourceFolders.add(
						fProject.getProjectBase().getFolder(sourceFolder));
			}
		}
		
		libFolders.clear();
		if (pathsStore.libFolders != null) {
			for (String libFolder : pathsStore.libFolders) {
				libFolders.add(
						fProject.getProjectBase().getFolder(libFolder));
			}
		}
		
		libFiles.clear();
		if (pathsStore.libFiles != null) {
			for (String libFile : pathsStore.libFiles) {
				libFiles.add(fProject.getProjectBase().getFile(libFile));
			}
		}
	}
	
	/**
	 * Method creates a path serializer.
	 * 	
	 * @return path serializer.
	 */
	private ProjectPathsSerializer createSerializer() {
		ProjectPathsSerializer pathsStore = new ProjectPathsSerializer();
		
		pathsStore.outputFolder = 
			outputFolder.getProjectRelativePath().toOSString();
		
		int arrayIndex;
		
		pathsStore.sourceFolders = new String[sourceFolders.size()];
		arrayIndex = 0;
		for (IFolder folder : sourceFolders) {
			pathsStore.sourceFolders[arrayIndex] = 
				folder.getProjectRelativePath().toOSString();
			arrayIndex++;
		}
	
		pathsStore.libFiles = new String[libFiles.size()];
		arrayIndex = 0;
		for (IFile file : libFiles) {
			pathsStore.libFiles[arrayIndex] = 
				file.getProjectRelativePath().toOSString();
			arrayIndex++;
		}
		
		pathsStore.libFolders = new String[libFolders.size()];
		arrayIndex = 0;
		for (IFolder folder : libFolders) {
			pathsStore.libFolders[arrayIndex] = 
				folder.getProjectRelativePath().toOSString();
			arrayIndex++;
		}
		
		return pathsStore;
	}
	
	/**
	 * Stores all paths to the file.	
	 */
	public void store() {
		try {
			JAXBContext context = JAXBContext.newInstance(
					ProjectPathsSerializer.class);
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
		    IFile file = fProject.getProjectBase().getFile(fileName);
		    
		    ByteArrayOutputStream os = new ByteArrayOutputStream();
		    
		    marshaller.marshal(createSerializer(), os);
		    
		    InputStream stream = new ByteArrayInputStream(os.toByteArray());
		    
		    if (file.exists()) {
		    	file.setContents(stream, IFile.FORCE, null);
		    } else {
		    	file.create(stream, true, null);
		    }
			
		
		} catch (JAXBException e) {
			EclihxCore.getLogHelper().logError(e);
		
		} catch (CoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}
		
	}
	
	/**
	 * Creates a manager for the Haxe project.
	 * 
	 * @param project Haxe project manager is belong to.
	 * @return new project path manager.
	 */
	static public IProjectPathManager create(IHaxeProject project) {
		IFile file = project.getProjectBase().getFile(fileName);
		ProjectPathManager pathManager = null;
		
		if (file.exists()) {
			// Try to load stored data
			InputStream is;
			
			try {
				JAXBContext context = JAXBContext.newInstance(
						ProjectPathsSerializer.class);
			    Unmarshaller unmarshaller = context.createUnmarshaller();
			    
			    is = file.getContents(true);
			    
				ProjectPathsSerializer pps = 
						(ProjectPathsSerializer)unmarshaller.unmarshal(is);
			    
			    return (new ProjectPathManager(project, pps));
			} catch (JAXBException e) {
				EclihxCore.getLogHelper().logError(e);
			} catch (CoreException e) {
				EclihxCore.getLogHelper().logError(e);
			}
		}
		
		if (pathManager == null) {
			// If loading failed we create manager with default values;
			pathManager = new ProjectPathManager(project);
		}
		
		return pathManager;
	}
	
	/**
	 * Add new library folder.
	 * 
	 * @param folder the folder to add. 
	 */
	public void addLibFolder(IFolder folder) {
		Assert.isTrue(folder.getProject().equals(fProject.getProjectBase()));
		
		libFolders.add(folder);
	}

	/**
	 * Add new source folder.
	 * 
	 * @param folder the folder to add.
	 */
	public void addSourceFolder(IFolder folder) {
		Assert.isTrue(folder.getProject().equals(fProject.getProjectBase()));
		
		sourceFolders.add(folder);
		
		// For now allow only one source folder
		// TODO 4 think of creating several source folders in project
		assert(sourceFolders.size() == 1);   
	}

	/**
	 * Get library folders.
	 */
	public List<IFolder> getLibFolders() {
		return libFolders;
	}

	/**
	 * Get output folder.
	 */
	public IFolder getOutputFolder() {
		return outputFolder;
	}

	/**
	 * Get source folders.
	 */
	public List<IFolder> getSourceFolders() {
		return sourceFolders;
	}

	/**
	 * Update list of library folders.
	 */
	public void setLibFolders(IFolder[] folders) {
		libFolders.clear();
		for (IFolder folder : folders) {
			addLibFolder(folder);
		}
	}
	
	/**
	 * Set output folder.
	 */
	public void setOutputFolder(IFolder folder) {
		outputFolder = folder;		
	}
	
	/**
	 * Update list of source folders.
	 */
	public void setSourceFolders(IFolder[] folders) {
		sourceFolders.clear();
		for (IFolder folder : folders) {
			addSourceFolder(folder);
		}
	}
}
