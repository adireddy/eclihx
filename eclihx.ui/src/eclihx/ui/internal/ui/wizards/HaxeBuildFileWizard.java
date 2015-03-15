package eclihx.ui.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import eclihx.core.haxe.model.core.IHaxeBuildFile;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.ui.wizards.NewBuildFileWizardPage;

/**
 * Wizard for Haxe build file.
 */
public class HaxeBuildFileWizard extends AbstractMonitorWizard implements INewWizard {

	/**
	 * The only page for file creation.
	 */
	NewBuildFileWizardPage buildFilePage;
	
	/**
	 * The workbench selection.
	 */
	IStructuredSelection selection;

	/**
	 * Workbench.
	 */
	//private IWorkbench fWorkbench;
	
	/**
	 * Default constructor.
	 */
	public HaxeBuildFileWizard() {
		super();
		
		setWindowTitle("New Haxe Build File");
	}
	
	@Override
	protected void doCancel(IProgressMonitor monitor) {
	}

	@Override
	protected void doFinish(IProgressMonitor monitor) {
		
		final IHaxeProject haxeProject = buildFilePage.getHaxeProject();
		final String buildFileName = buildFilePage.getBuildFileName();
		
		monitor.beginTask("Creating build file " + buildFileName, 1);
		
		try {
			IHaxeBuildFile buildFile = 
					haxeProject.createBuildFile(buildFileName, monitor);
			
			if (buildFile != null) {
				//BasicNewResourceWizard.selectAndReveal(
				//		buildFile.getBaseResource(), 
				//		fWorkbench.getActiveWorkbenchWindow());
				
				openFile(buildFile.getBaseFile());				
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

		buildFilePage = new NewBuildFileWizardPage(
				"NewBuildFileWizardPage", selection);
		addPage(buildFilePage);
	}	
}
