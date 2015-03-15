package eclihx.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.DeleteResourceAction;

import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Delete action for the Haxe elements.
 */
public class DeleteHaxeElementAction
		extends SelectionDispatchAction implements IShellProvider {

	/**
	 * Default constructor with the workbench site.
	 * @param site the site where action should be constructed.
	 */
	public DeleteHaxeElementAction(IWorkbenchSite site) {
		super(site);
		
		setText("Delete");
		
		setDescription("Deletes the selected Haxe element."); 
		
		ISharedImages workbenchImages = 
			EclihxUIPlugin.getDefault().getWorkbench().getSharedImages();
		
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
	}

	
	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.actions.SelectionDispatchAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		if (selection.isEmpty()) {
			return false;
		}
		
		if (!selection.isEmpty()) {
			for (Iterator<?> iter = selection.iterator(); 
					iter.hasNext();) {
				Object element = iter.next();
				
				if (element instanceof IHaxePackage && 
						((IHaxePackage)element).isDefault()) {
					// Disable for default packages.
					return false;					
				}
				
				// TODO 7 Make delete action work correctly for these elements.
				if (element instanceof IHaxeSourceFolder || 
						element instanceof IHaxeOutputFolder) {
					return false;
				}
					
				if (element instanceof IAdaptable) { 
					IResource resource = 
							(IResource)((IAdaptable)element).getAdapter(
									IResource.class);
					if (resource == null) {
						return true;
					}
					
					if (resource.getType() == IResource.PROJECT && 
							!((IProject)resource).isOpen()) {
						return false;
					}					
				}
			}
		}
		
		// Enable if we hadn't found a reason to disable.
		return true;
	}

	/**
	 * Creates and initializes the base workbench action. 
	 * @param selection initial selection.
	 * @return new DeleteResourceAction object
	 */
	private IAction createWorkbenchAction(IStructuredSelection selection) {
		DeleteResourceAction action= new DeleteResourceAction(this);
		action.selectionChanged(selection);
		return action;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.actions.SelectionDispatchAction#run(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void run(IStructuredSelection selection) {
		createWorkbenchAction(selection).run();
	}	
}

