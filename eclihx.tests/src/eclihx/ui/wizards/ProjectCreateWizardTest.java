package eclihx.ui.wizards;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.junit.Test;

import eclihx.ui.internal.ui.wizards.HaxePackageWizard;
import eclihx.ui.internal.ui.wizards.HaxeProjectWizard;

/**
 * Tests for {@link HaxePackageWizard}
 */
public class ProjectCreateWizardTest {

	/**
	 * Open-close test for haXe project creation wizard.
	 * @throws WorkbenchException opening page error.
	 */
	@Test
	public void openCloseTest() throws WorkbenchException {
		
		HaxeProjectWizard wizard = new HaxeProjectWizard();
		wizard.init(PlatformUI.getWorkbench(), null);
		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		dialog.close();
	}
}
