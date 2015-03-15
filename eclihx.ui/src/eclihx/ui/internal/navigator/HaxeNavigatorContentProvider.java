package eclihx.ui.internal.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.HaxeProject;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.HaxeElementsContentProvider;

/**
 * Content provider for Haxe navigator view.
 */
public class HaxeNavigatorContentProvider extends HaxeElementsContentProvider {

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.utils.HaxeElementsContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		
		// If it's a root - return all projects.
		if (parentElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot) parentElement;
			return root.getProjects();
		}
		
		if (parentElement instanceof IProject) {
			IProject project= (IProject) parentElement;
			if (project.isAccessible()) {
				if (HaxeProject.isHaxeProject(project)) {
					return super.getChildren(
						EclihxCore.getDefault().getHaxeWorkspace().getHaxeProject(
								project));
				} else {
					
					try {
						return project.members();
					} catch (CoreException e) {
						EclihxUIPlugin.getLogHelper().logError(e);
						return NO_CHILDREN;
					}
					
				}				
				
			}
		}
		
		return super.getChildren(parentElement);
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.utils.HaxeElementsContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		Object parent= super.getParent(element);
		if (parent instanceof IHaxeProject) {
			return ((IHaxeProject)parent).getProjectBase();
		}
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.utils.HaxeElementsContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			return ((IProject) element).isAccessible();
		}
		return super.hasChildren(element);
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.utils.HaxeElementsContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot) inputElement;
			return root.getProjects();
		}
		if (inputElement instanceof IProject) {
			if (HaxeProject.isHaxeProject((IProject)inputElement)) {
				return super.getElements(
						EclihxCore.getDefault().getHaxeWorkspace().
								getHaxeProject((IProject)inputElement));
			}			
		}
		return super.getElements(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.utils.HaxeElementsContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
