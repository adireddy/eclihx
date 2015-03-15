package eclihx.ui.internal.ui.views;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.core.IHaxeElement;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeWorkspace;
import eclihx.ui.internal.navigator.HaxeNavigatorComparer;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * View for the Haxe projects structure.
 */
public class HaxeExplorerView extends CommonNavigator implements
		IResourceChangeListener {

	/** The explorer view id. */
	public static final String HAXE_EXPLORER_ID = 
			"eclihx.ui.internal.ui.views.PackageExplorerView";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonNavigator#createPartControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		getCommonViewer().setComparer(HaxeNavigatorComparer.INSTANCE);
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.navigator.CommonNavigator#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		getCommonViewer().setComparer(HaxeNavigatorComparer.INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
					 */
					@Override
					public boolean visit(final IResourceDelta delta)
							throws CoreException {

						if (delta.getResource() != null) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									
									final IHaxeWorkspace haxeWorkspace = 
										EclihxCore.getDefault().getHaxeWorkspace();
								
									final IHaxeElement haxeElement = 
										haxeWorkspace.getHaxeElement(
												delta.getResource());
									
									if (haxeElement != null && 
										!(haxeElement instanceof IHaxeProject || 
										  haxeElement instanceof IHaxeWorkspace)) {
										getCommonViewer().refresh(
												haxeElement);
										getCommonViewer().expandToLevel(
												haxeElement, 0);										
									} else {
										getCommonViewer().refresh(
												delta.getResource());
										getCommonViewer().expandToLevel(
												delta.getResource(), 0);
									}
									
									
								}
							});
							
							return true;
						}
						
						return false;
					}
					
				});
			} catch (CoreException e) {
				EclihxUIPlugin.getLogHelper().logError(e);
			}
		}
	}

}
