/**
 * 
 */
package eclihx.ui.internal.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * Action provider for navigator with Haxe elements.
 */
public class HaxeNavigatorActionProvider extends CommonActionProvider {

	private EditActionGroup fEditGroup;
	
	private boolean fInViewPart = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
	 */
	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);
		
		ICommonViewerWorkbenchSite workbenchSite = null;
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
		}

		if (workbenchSite != null) {
			if (workbenchSite.getPart() != null && workbenchSite.getPart() 
					instanceof IViewPart) {
				
				IViewPart viewPart = (IViewPart) workbenchSite.getPart();
				
				fEditGroup = new EditActionGroup(viewPart);
				
				fInViewPart = true;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (fInViewPart) {
			fEditGroup.fillActionBars(actionBars);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (fInViewPart) {
			fEditGroup.fillContextMenu(menu);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#setContext(org.eclipse.ui.actions.ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		if (fInViewPart) {
			fEditGroup.setContext(context);
		}
	}
	
}
