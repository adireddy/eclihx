package eclihx.ui.internal.ui.utils;

import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.WorkbenchContentProvider;

import eclihx.core.haxe.model.core.IHaxeElement;
import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.core.haxe.model.core.IHaxeWorkspace;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.HaxeElementFilter.ShowElement;

/**
 * Class provides an information for tree representation of the 
 * all Haxe elements.
 * 
 * TODO 6 Make it work not only for the IHaxeProject and IHaxeSourceFolder
 */
public class HaxeElementsContentProvider implements ITreeContentProvider {
	
	/**
	 * For showing standard resources.
	 */
	private final WorkbenchContentProvider workbenchContentProvider;
	
	/**
	 *  Default value for the elements without children.
	 */
	protected final static Object[] NO_CHILDREN = new Object[0];
	
	/**
	 * Filters the Haxe elements to show.
	 */
	private final HaxeElementFilter elementsFilter;

	/**
	 * Default provider which will show all elements.
	 */
	public HaxeElementsContentProvider() {
		this(new HaxeElementFilter(ShowElement.All));		
	}
	
	/**
	 * Provider with the filter of elements to show.
	 * @param elementsFilter the filter for elements.
	 */
	public HaxeElementsContentProvider(
			HaxeElementFilter elementsFilter) {
		
		if (elementsFilter == null) {
			throw new NullPointerException("Filter parameter can't be null");
		}

		workbenchContentProvider = new WorkbenchContentProvider();
		
		this.elementsFilter = elementsFilter;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof IHaxeWorkspace) { 
			return ((IHaxeWorkspace)parentElement).getHaxeProjects();
		} 
		
		if (parentElement instanceof IHaxeProject) {
			
			final IHaxeProject haxeProject = (IHaxeProject)parentElement;
			final LinkedList<Object> children = new LinkedList<Object>();
			
			// Append build files.
			if (elementsFilter.showBuildFiles()) {
				try {
					children.addAll(
							Arrays.asList(haxeProject.getBuildFilesElements()));
				} catch (CoreException e) {
					EclihxUIPlugin.getLogHelper().logError(e);
				}
			}
			
			// Append source folders.
			if (elementsFilter.showSourceFolder()) {
				children.addAll(
						Arrays.asList(haxeProject.getSourceFolders()));
			}
			
			// Append output folder of the project.
			if (elementsFilter.showOutputFolder()) {
				children.add(haxeProject.getOutputFolder());
			}			
			
			return children.toArray();					
		} 
		
		if (parentElement instanceof IHaxeSourceFolder) {
			return ((IHaxeSourceFolder)parentElement).getPackages();
		}
		
		if (parentElement instanceof IHaxePackage) {
			final IHaxePackage haxePackage = (IHaxePackage)parentElement;
			final LinkedList<Object> children = new LinkedList<Object>();
			
			if (elementsFilter.showSourceFiles()) {
				children.addAll(
						Arrays.asList(haxePackage.getHaxeSourceFiles()));
			}
			
			return children.toArray();
		}
		
		if (parentElement instanceof IHaxeOutputFolder) {
			if (elementsFilter.showOutputChildren()) {
				return workbenchContentProvider.getChildren(
						((IHaxeOutputFolder)parentElement).getBaseFolder());
			}
		}
		
		if (parentElement instanceof IResource) {
			return workbenchContentProvider.getChildren(parentElement);
		}

		return NO_CHILDREN;	
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof IHaxeElement) {
			return ((IHaxeElement)element).getParent();
		} 
		
		return workbenchContentProvider.getParent(element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		
		return ((element instanceof IHaxeWorkspace && 
						elementsFilter.showWorkspaceChildren()) || 
				(element instanceof IHaxeProject && 
						elementsFilter.showProjectsChildren()) ||
				(element instanceof IHaxeSourceFolder &&
						elementsFilter.showSourceFolderChildren()) ||
				(element instanceof IHaxePackage && 
						elementsFilter.showPackageChildren() &&
						!((IHaxePackage)element).isEmpty()) ||
				(element instanceof IHaxeOutputFolder &&
						elementsFilter.showOutputChildren()) ||
				(element instanceof IHaxeSourceFolder &&
						elementsFilter.showOutputChildren()) ||
				(element instanceof IResource &&
						workbenchContentProvider.hasChildren(element)));
	
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		workbenchContentProvider.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}	
}
