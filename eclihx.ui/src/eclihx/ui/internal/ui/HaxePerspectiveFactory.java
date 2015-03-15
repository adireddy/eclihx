package eclihx.ui.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import eclihx.ui.internal.ui.views.HaxeExplorerView;

/**
 * Class for building Haxe perspective
 */
public class HaxePerspectiveFactory implements IPerspectiveFactory {

	/**
	 * ID of the Haxe perspective.
	 */
	public final static String HAXE_PERSPECTIVE_ID = 
			"eclihx.ui.internal.ui.HaxePerspectiveFactory";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		// Top left
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.25, editorArea);
		topLeft.addView(HaxeExplorerView.HAXE_EXPLORER_ID);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		// Bottom right
		IFolderLayout bottomRight = layout.createFolder(
				"bottomRight", IPageLayout.BOTTOM, (float) 0.65, editorArea);
		bottomRight.addView(IPageLayout.ID_TASK_LIST);
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(HaxeExplorerView.HAXE_EXPLORER_ID);
				
		layout.addNewWizardShortcut(
				"eclihx.ui.internal.ui.wizards.HaxeProjectWizard");
		
		layout.addNewWizardShortcut(
				"eclihx.ui.internal.ui.wizards.HaxeFileWizard");
		
		layout.addNewWizardShortcut(
				"eclihx.ui.internal.ui.wizards.HaxeBuildFileWizard");
		
		layout.addNewWizardShortcut(
				"eclihx.ui.internal.ui.wizards.HaxePackageWizard");
		
	}
}