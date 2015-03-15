package eclihx.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.StandardDialogs;
import eclihx.ui.internal.ui.wizards.HaxeBuildFileWizard;

/**
 * Page for the build file creation. This page is used in
 * {@link HaxeBuildFileWizard}.
 */
public final class NewBuildFileWizardPage extends AbstractSelectionPage {

	/**
	 * Selected Haxe project.
	 */
	IHaxeProject haxeProject;

	/**
	 * Field with the Haxe project name.
	 */
	Text haxeProjectField;

	/**
	 * The field for the build file name.
	 */
	Text buildFileField;

	/**
	 * Default constructor with the page name and selection.
	 * 
	 * @param pageName the name of the page.
	 * @param selection the workbench selection on the period of wizard
	 *        activation.
	 */
	public NewBuildFileWizardPage(String pageName,
			IStructuredSelection selection) {

		super("Haxe Build File", selection);

		setDescription("Insert a name for the new Haxe build file");
		setTitle("Creation of the Haxe build file");
	}

	@Override
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);

		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(3, false));

		{
			// Add Haxe project group

			Label label = new Label(top, SWT.NULL);
			label.setText("Project:");

			haxeProjectField = new Text(top, SWT.BORDER);
			haxeProjectField.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			haxeProjectField.setEditable(false); // Do not allow modification.
			haxeProjectField.addModifyListener(fUpdateDialogListener);

			Button projectSelectionButton = new Button(top, SWT.NULL);
			projectSelectionButton.setText("Browse...");

			projectSelectionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onHaxeProjectsBrowseButton();
				}
			});
		}

		{
			// Package name group

			Label label = new Label(top, SWT.NULL);
			label.setText("Build File Name:");

			buildFileField = new Text(top, SWT.BORDER | SWT.SINGLE);

			buildFileField
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			buildFileField.addModifyListener(fUpdateDialogListener);
		}

		initialize();

		// Force remove message if the situation is invalid.
		setErrorMessage(null);

		setControl(top);

	}

	/**
	 * Method sets initial values of the fields according to the current
	 * selection.
	 */
	@Override
	protected void initialize() {

		// Reset fields.
		buildFileField.setText("");
		updateHaxeProject(null);		

		// Examine selection.
		if (!(selection == null || selection.isEmpty())) {
			IStructuredSelection structedSelection = selection;

			// For the case when single element was selected
			if (structedSelection.size() == 1) {

				Object selectedElement = structedSelection
						.getFirstElement();

				updateHaxeProject(
						SelectionUtils.getHaxeProjectFromSelection(
								selectedElement));
			}
		}
	}
	
	/**
	 * Method updates the selected Haxe project.
	 * 
	 * @param project new Haxe project.
	 */
	private void updateHaxeProject(IHaxeProject project) {
		haxeProject = project;
		
		haxeProjectField.setText(
				haxeProject != null ? haxeProject.getName() : "");
		
	}

	/**
	 * Handler of the Haxe projects browse button.
	 */
	private void onHaxeProjectsBrowseButton() {

		ElementTreeSelectionDialog dialog = 
				StandardDialogs.createHaxeProjectsDialog(getShell());

		if (dialog.open() == Window.OK) {
			
			Object result = dialog.getFirstResult();
			
			if (result instanceof IHaxeProject) {
				updateHaxeProject((IHaxeProject)result);
			} else {
				EclihxUIPlugin.getLogHelper().logError(
						"Ivalid result value in " + 
						"NewBuildFileWizardPage.onHaxeProjectsBrowseButton");
			}			
		}
	}

	/**
	 * Validates the project and package fields and updates status of the
	 * page.
	 */
	@Override
	protected void dialogChangedHandler() {
		
		// Project validation
		if (haxeProject == null) {
			updateStatus("Project is not selected.");
			return;
		}
		
		String buildFileName = getBuildFileName();
		
		IStatus fileValidateStatus = HaxeElementValidator.
				validateBuildFileName(buildFileName); 
				
		if (!fileValidateStatus.isOK()) {
			updateStatus(fileValidateStatus.getMessage());
			return;
		}
		
		if (haxeProject.hasBuildFile(buildFileName)) {
			updateStatus("Such build file already exists.");
			return;
		}
		
		super.dialogChangedHandler();
	}
	
	/**
	 * Returns selected Haxe project
	 * @return the Haxe project.
	 */
	public IHaxeProject getHaxeProject() {
		return haxeProject;
	}
	
	/**
	 * Returns selected build file name.
	 * @return the selected build file name.
	 */
	public String getBuildFileName() {
		return buildFileField.getText().endsWith(".hxml") ? buildFileField.getText() : buildFileField.getText() + ".hxml";
	}	
	
}
