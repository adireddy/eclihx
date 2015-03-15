package eclihx.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.OpenFileAction;

import eclihx.core.haxe.model.core.IHaxeElement;


/**
 * Open action for the Haxe element.
 * Action works with the elements wrapping the {@link IFile} resource and
 * it redirects request to the {@link OpenFileAction} giving the wrapped
 * resource as a parameter.
 */
public class OpenHaxeElementAction extends SelectionDispatchAction {

	/**
	 * Default constructor with the workbench site.
	 * @param site the site where action should be constructed.
	 */
	public OpenHaxeElementAction(IWorkbenchSite site) {
		super(site);
		setText("Open");
		setDescription("Open element.");
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
				
				if (!(element instanceof IHaxeElement && 
						((IHaxeElement)element).getBaseResource() 
								instanceof IFile)) {
					return false;					
				}
			}
		}
		
		// Enable if we hadn't found a reason to disable.
		return true;
	}

	/**
	 * Creates and initializes the base workbench open action. 
	 * @param selection initial selection.
	 * @return new DeleteResourceAction object
	 */
	private IAction createWorkbenchAction(IStructuredSelection selection) {
		OpenFileAction action = new OpenFileAction(getSite().getPage());
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

