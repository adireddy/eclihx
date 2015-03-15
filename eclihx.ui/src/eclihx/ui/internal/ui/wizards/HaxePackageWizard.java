package eclihx.ui.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.ui.PluginImages;
import eclihx.ui.wizards.NewHaxePackageWizardPage;

/**
 * Wizard for the Haxe package.
 */
public class HaxePackageWizard extends 
		AbstractMonitorWizard implements INewWizard {
	
	/**
	 * Store the reference to the page.
	 */
	private NewHaxePackageWizardPage page;

	/**
	 * Stores the selection. 
	 */
	private IStructuredSelection selection;

	/**
	 * Constructor for HaxePackageWizard.
	 */
	public HaxePackageWizard() {
		
		super();
		
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWPACK);
		setWindowTitle("New Haxe Package");
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		page = new NewHaxePackageWizardPage(selection);
		addPage(page);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.wizards.AbstractMonitorWizard#doFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doFinish(IProgressMonitor monitor) {
		
		final String packageName = page.getPackageName();
		final IHaxeSourceFolder folder = page.getSourceFolder();
		
		monitor.beginTask("Creating package " + packageName, 1);
		
		try {
			folder.createPackage(packageName, monitor);
		} catch (CoreException e) {
			showErrorBox(e.getMessage());
		}
		
		monitor.worked(1);
	}
	
	@Override
	protected void doCancel(IProgressMonitor monitor) {
		// TODO 5 Remove created resource
	}
}
