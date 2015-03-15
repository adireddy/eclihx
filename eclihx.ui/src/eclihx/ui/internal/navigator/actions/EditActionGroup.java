package eclihx.ui.internal.navigator.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import eclihx.ui.actions.DeleteHaxeElementAction;
import eclihx.ui.actions.SelectionDispatchAction;

/**
 * Haxe elements edit group actions.
 * 
 * By now only Delete action is added to this group.
 */
public class EditActionGroup extends ActionGroup {
	
 	private final SelectionDispatchAction[] fActions;

 	private final SelectionDispatchAction fDeleteAction;
 	
	private final ISelectionProvider fSelectionProvider;

 	/**
	 * Creates new edit group. The group requires that
	 * the selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the view part that owns this action group
	 */
	public EditActionGroup(IViewPart part) {
		this(part.getSite(), null);
	}
	
	/**
	 * Creates new edit group. The group requires that
	 * the selection provided by the page's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param page the page that owns this action group
	 */
	public EditActionGroup(Page page) {
		this(page.getSite(), null);
	}

	/**
	 * Creates new edit group. The group requires
	 * that the selection provided by the given selection provider is of type 
	 * {@link IStructuredSelection}.
	 * 
	 * @param site the site that will own the action group.
	 * @param specialSelectionProvider the selection provider used instead of the
	 *  sites selection provider.
	 */
	public EditActionGroup(
			IWorkbenchSite site, ISelectionProvider specialSelectionProvider) {
		fSelectionProvider = 
				specialSelectionProvider == null ? site.getSelectionProvider() : 
						specialSelectionProvider;
		
		fDeleteAction = new DeleteHaxeElementAction(site);
		fDeleteAction.setActionDefinitionId(
				IWorkbenchActionDefinitionIds.DELETE);
		
		fActions= new SelectionDispatchAction[] { fDeleteAction };
		if (specialSelectionProvider != null) {
			for (int i= 0; i < fActions.length; i++) {
				fActions[i].setSpecialSelectionProvider(
						specialSelectionProvider);
			}
		}
		
		registerActionsAsSelectionChangeListeners();
	}

	private void registerActionsAsSelectionChangeListeners() {
		ISelectionProvider provider= fSelectionProvider;
		ISelection selection= provider.getSelection();
		for (int i= 0; i < fActions.length; i++) {
			SelectionDispatchAction action= fActions[i];
			action.update(selection);
			provider.addSelectionChangedListener(action);
		}
	}
	
	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider= fSelectionProvider;
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}
	
	/**
	 * Returns the delete action managed by this action group. 
	 * 
	 * @return the delete action. Returns <code>null</code> if the group
	 * doesn't provide any delete action
	 */
	public IAction getDeleteAction() {
		return fDeleteAction;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(
				ActionFactory.DELETE.getId(), fDeleteAction);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		for (int i= 0; i < fActions.length; i++) {
			SelectionDispatchAction action= fActions[i];
			menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);
		}		
	}		
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		deregisterActionsAsSelectionChangeListeners();
	}

}
