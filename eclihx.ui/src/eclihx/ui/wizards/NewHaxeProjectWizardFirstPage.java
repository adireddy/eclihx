package eclihx.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import eclihx.core.CorePreferenceInitializer;
import eclihx.core.EclihxCore;
import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.model.core.IHaxeProject;

/**
 * First page of the Haxe project creation wizard.
 */
public class NewHaxeProjectWizardFirstPage 
		extends WizardNewProjectCreationPage {
	
	/**
	 * Build file name field.
	 */
	private Text buildFileField;
	
	/**
	 * Source folder field.
	 */
	private Text srcFolderField;
	
	/**
	 * Output folder field.
	 */
	private Text outputFolderField;

	/**
	 * Validation on modifying attributes
	 */
	private final Listener propertiesModifyListener = new Listener() {
		public void handleEvent(Event e) {
			setPageComplete(validatePage());
		}
	};

	/**
	 * Default constructor.
	 */
	public NewHaxeProjectWizardFirstPage() {
		super("New Haxe project");
		setTitle("Create a Haxe project");
		setDescription(
				"Create a Haxe project in the workspace or in an " +
				"external location.");
		checkCompiler();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		Group group = new Group((Composite) getControl(), SWT.NONE);
		group.setText("Project settings");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		// common data grid
		GridData dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);

		// build file label
		Label buildFileLabel = new Label(group, SWT.NONE);
		buildFileLabel.setText("Build File (hxml):");
		buildFileLabel.setFont(parent.getFont());

		// build file entry field
		buildFileField = new Text(group, SWT.BORDER);
		buildFileField.setText(IHaxeProject.DEFAULT_BUILD_FILE_NAME);
		buildFileField.setFont(parent.getFont());
		buildFileField.setLayoutData(dataFillHorizontal);
		buildFileField.addListener(SWT.Modify, propertiesModifyListener);
		
		// source folder label
		Label srcFolderLabel = new Label(group, SWT.NONE);
		srcFolderLabel.setText("Source Folder:");
		srcFolderLabel.setFont(parent.getFont());

		// source folder entry field
		srcFolderField = new Text(group, SWT.BORDER);
		srcFolderField.setText(IHaxeProject.DEFAULT_SOURCE_FOLDER_NAME);
		srcFolderField.setFont(parent.getFont());
		srcFolderField.setLayoutData(dataFillHorizontal);
		srcFolderField.addListener(SWT.Modify, propertiesModifyListener);

		// Output folder label
		Label binFolderLabel = new Label(group, SWT.NONE);
		binFolderLabel.setText("Output Folder:");
		binFolderLabel.setFont(parent.getFont());
	
		// output folder entry field
		outputFolderField = new Text(group, SWT.BORDER);
		outputFolderField.setText(IHaxeProject.DEFAULT_OUTPUT_FOLDER_NAME);
		outputFolderField.setFont(parent.getFont());
		outputFolderField.setLayoutData(dataFillHorizontal);
		outputFolderField.addListener(SWT.Modify, propertiesModifyListener);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#validatePage()
	 */
	@Override
	protected boolean validatePage() {
		
		// Validates the project name and project location fields.
		if (!super.validatePage()) {
			return false;
		}
		
		IStatus validateStatus;

		// Validates the build file name.
		validateStatus = 
			HaxeElementValidator.validateBuildFileName(getBuildFileName());
		
		if (!validateStatus.isOK()) {
			setErrorMessage(validateStatus.getMessage());
			return false;
		}
		
		// Validates the source folder 
		validateStatus = 
			HaxeElementValidator.validateHaxeSourceFolderName(
					getSourceFolderName());
		
		if (!validateStatus.isOK()) {
			setErrorMessage(validateStatus.getMessage());
			return false;
		}
		
		// Validates the output folder 
		validateStatus = 
			HaxeElementValidator.validateHaxeSourceFolderName(
					getOutputFolderName());
		
		if (!validateStatus.isOK()) {
			setErrorMessage(validateStatus.getMessage());
			return false;
		}		

		return true;
	}
	
	/**
	 * Returns the value of the project build file name field with leading and
	 * trailing spaces removed.
	 * 
	 * @return the entered build file name
	 */
	public String getBuildFileName() {
		return buildFileField.getText().trim();
	}

	/**
	 * Returns source folder name with leading and trailing spaces removed.
	 * 
	 * @return source folder name
	 */
	public String getSourceFolderName() {
		return srcFolderField.getText().trim();
	}

	/**
	 * Returns output folder with leading and trailing spaces removed.
	 * 
	 * @return output folder name
	 */
	public String getOutputFolderName() {
		return outputFolderField.getText().trim();
	}
	
 	/**
	 * Check the Compiler field.
	 */
	public void checkCompiler() {
		if (EclihxCore.getDefault().getPluginPreferences().getString(
				CorePreferenceInitializer.HAXE_COMPILER_PATH).isEmpty()) {
			
			MessageDialog.openWarning(getShell(), "Missing Compiler Path",
					"Please, define Haxe compiler first " +
		    			"(Preferences->Eclihx->Compiler).");
		}
	}

}
