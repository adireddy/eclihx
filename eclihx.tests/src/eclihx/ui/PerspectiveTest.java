package eclihx.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.junit.Test;

import eclihx.ui.internal.ui.HaxePerspectiveFactory;

/**
 * Test for opening haXe perspective
 */
public class PerspectiveTest {

	/**
	 * Open haXe perspective test.
	 * @throws WorkbenchException opening page error.
	 */
	@Test
	public void perspectiveOpen() throws WorkbenchException {
		IWorkbenchWindow activeWorkbenchWindow = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		page.close();
		page = activeWorkbenchWindow.openPage(
				HaxePerspectiveFactory.HAXE_PERSPECTIVE_ID, 
				ResourcesPlugin.getWorkspace().getRoot());
	}
}
