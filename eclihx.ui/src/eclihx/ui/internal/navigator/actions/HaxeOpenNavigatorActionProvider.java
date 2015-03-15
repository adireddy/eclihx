package eclihx.ui.internal.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import eclihx.ui.actions.OpenHaxeElementAction;

/**
 * Action provider for Haxe element open actions.
 * Current version supports only opening of the Haxe elements with the
 * wrapped file resources. 
 */
public class HaxeOpenNavigatorActionProvider extends CommonActionProvider {

	/**
	 * Open action object which process open requests.
	 */
	private OpenHaxeElementAction openAction;

	/**
	 * Default constructor for action provider.
	 */
	public HaxeOpenNavigatorActionProvider() { 		
	}	
	
	
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
				openAction = new OpenHaxeElementAction(viewPart.getViewSite());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) { 
		/* Set up the property open action when enabled. */
		if(openAction.isEnabled())
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		if(openAction.isEnabled())
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);		
	}
}
