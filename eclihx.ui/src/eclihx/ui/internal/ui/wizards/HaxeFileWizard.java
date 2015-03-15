package eclihx.ui.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import eclihx.core.haxe.internal.HaxeContentManager;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeSourceFile;
import eclihx.ui.PreferenceConstants;
import eclihx.ui.wizards.NewHaxeFileWizardPage;

/**
 * Wizard for the new Haxe file.
 */
public class HaxeFileWizard extends AbstractMonitorWizard implements INewWizard {

	/**
	 * The only page for Haxe file creation.
	 */
	NewHaxeFileWizardPage haxeFilePage;
	
	/**
	 * The workbench selection.
	 */
	IStructuredSelection selection;
	
	/**
	 * Default constructor.
	 */
	public HaxeFileWizard() {
		super();
		
		setWindowTitle("New Haxe file");
	}
	
	@Override
	protected void doCancel(IProgressMonitor monitor) {
	}

	@Override
	protected void doFinish(IProgressMonitor monitor) {
		
		final IHaxePackage haxePackage = haxeFilePage.getHaxePackage();
		final String haxeFileName = haxeFilePage.getFileName();
		
		monitor.beginTask("Creating Haxe file " + haxeFileName, 1);
		
		try {
			// TODO 3: Remove that! Singleton usage should be removed. 
			HaxeContentManager.getInstance().setEmptyPackagesEnabled(
					PreferenceConstants.getPreferenceStore().getBoolean(
							PreferenceConstants.HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE));
						
			IHaxeSourceFile sourceFile =
				haxePackage.createHaxeFile(haxeFileName, monitor);
		
			if (sourceFile != null) {
				//BasicNewResourceWizard.selectAndReveal(
				//		buildFile.getBaseResource(), 
				//		fWorkbench.getActiveWorkbenchWindow());
				
				openFile(sourceFile.getBaseFile());				
			}
		
		} catch (CoreException e) {
			showErrorBox(e.getMessage());
		}
		
		monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		
		super.addPages();

		haxeFilePage = new NewHaxeFileWizardPage(
				"NewBuildFileWizardPage", selection);
		addPage(haxeFilePage);
	}	
}