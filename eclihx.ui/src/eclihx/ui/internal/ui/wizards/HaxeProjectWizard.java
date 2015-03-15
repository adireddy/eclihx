package eclihx.ui.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import eclihx.core.haxe.model.helper.ProjectCreator;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.wizards.NewHaxeProjectWizardFirstPage;

/**
 * Wizard for the Haxe project creation. 
 */
public class HaxeProjectWizard extends AbstractMonitorWizard implements INewWizard, IExecutableExtension {

	/**
	 * The wizard page with the new project data definition fields.
	 */
	private NewHaxeProjectWizardFirstPage firstPage; 
	
	/**
	 * Store configuration element.
	 */
	private IConfigurationElement fConfigElement;
	
	/**
	 * Default constructor.
	 */
	public HaxeProjectWizard() {
		super();
		
		setWindowTitle("New Haxe project");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInitializationData(
			IConfigurationElement config, 
			String propertyName, 
			Object data) throws CoreException {
		
		fConfigElement = config;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		firstPage = new NewHaxeProjectWizardFirstPage();
		
		super.addPage(firstPage);
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.wizards.AbstractMonitorWizard#doCancel(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doCancel(IProgressMonitor monitor) {
		// TODO 6 Test with unreadable folders 
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.wizards.AbstractMonitorWizard#doFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doFinish(IProgressMonitor monitor) {
		
		try {
			
			ProjectCreator.createCommonProject(
					firstPage.getProjectName(), 
					firstPage.getBuildFileName(), 
					firstPage.getOutputFolderName(), 
					firstPage.getSourceFolderName(), 
					monitor);
			
			// Switch to Haxe perspective
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			
		} catch (CoreException e) {
			EclihxUIPlugin.getLogHelper().logError(e);
			showErrorBox(e.getMessage());
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see eclihx.ui.internal.ui.wizards.AbstractProjectRelativeWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// We don't need selection for this wizard.
	}
}
