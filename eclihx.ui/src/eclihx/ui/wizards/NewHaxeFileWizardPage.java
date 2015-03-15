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
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.StandardDialogs;

/**
 * Main page for the creation of the Haxe file. 
 */
public class NewHaxeFileWizardPage extends AbstractSelectionPage {
	
	/**
	 * The Haxe source folder.
	 */
	private IHaxeSourceFolder sourceFolder;
	
	/**
	 * Stores selected Haxe package.
	 */
	private IHaxePackage haxePackage;
	
	/**
	 * Source folder text.
	 */
	private Text sourceFolderTextField;
	
	/**
	 * Package name text editor.
	 */
	private Text packageTextField;
	
	/**
	 * Haxe name field.
	 */
	private Text haxeNameField;

	/**
	 * Package selection button.
	 */
	private Button packageSelectButton;
	
	/**
	 * Default constructor with the parameter of the selection.
	 * 
	 * @param pageName the name of the page. 
	 * @param selection original selection.
	 */
	public NewHaxeFileWizardPage(
			String pageName, IStructuredSelection selection) {
		
		super(pageName, selection);
		
		setTitle("New Haxe File");
		setDescription("This wizard creates a new Haxe file.");
	}

	/**
	 * Handler of the source folder browse button.
	 */
	private void onSourceFolderBrowseButton() {
		ElementTreeSelectionDialog dialog = 
			StandardDialogs.createHaxeSourceFoldersDialog(getShell());
		
		if (dialog.open() == Window.OK) {
			
			Object result = dialog.getFirstResult();
			
			if (result instanceof IHaxeSourceFolder) {
				
				IHaxeSourceFolder haxeSourceFolder = (IHaxeSourceFolder)result;
				
				updateSourceFolderField(haxeSourceFolder);

			} else {
				EclihxUIPlugin.getLogHelper().logError(
						"Ivalid result value in " + 
						"NewHaxePackageWizardPage.onSourceFolderBrowseButton");
			}
		}
		
	}
	
	/**
	 * Handler of the Haxe package select button click.
	 */
	private void onPackageSelectButton() {
		
		ElementListSelectionDialog dialog = 
			StandardDialogs.createHaxePackagesDialog(getShell(), sourceFolder);

		if (dialog.open() == Window.OK) {
			updatePackageField((IHaxePackage)dialog.getFirstResult());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		
		Composite top = new Composite(parent, SWT.LEFT);

		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(3, false));

		{
			// Add source folder group
			
			Label label = new Label(top, SWT.NULL);
			label.setText("Source folder:");
	
			sourceFolderTextField = new Text(top, SWT.BORDER);
			sourceFolderTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			sourceFolderTextField.setEditable(false); // Do not allow modification.
			sourceFolderTextField.addModifyListener(fUpdateDialogListener);
			
			
			Button sourceFolderBrowse = new Button(top, SWT.NULL);
			sourceFolderBrowse.setText("Browse...");
			
			sourceFolderBrowse.addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							onSourceFolderBrowseButton();
						}
					});		
		}
		
		{
			// Package name group
			
			Label label = new Label(top, SWT.NULL);
			label.setText("Package Name:");
	
			packageTextField = new Text(top, SWT.BORDER | SWT.SINGLE);
			
			packageTextField.setEditable(false);
			packageTextField.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));
			packageTextField.addModifyListener(fUpdateDialogListener);
			
			packageSelectButton = new Button(top, SWT.NULL);
			packageSelectButton.setEnabled(false);
			packageSelectButton.setText("Browse...");
			
			packageSelectButton.addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							onPackageSelectButton();
						}
					});
		}
		
		{
			// File name group
			
			Label label = new Label(top, SWT.NULL);
			label.setText("File Name:");
			
			haxeNameField = new Text(top, SWT.BORDER | SWT.SINGLE);
			haxeNameField.setFocus();
			
			haxeNameField.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));
			haxeNameField.addModifyListener(fUpdateDialogListener);
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
		haxeNameField.setText("");
		sourceFolderTextField.setText("");
		packageTextField.setText("");
		
		// Examine selection.
		if (!(selection == null || selection.isEmpty())) {
			
			IStructuredSelection structedSelection = selection;
			
			// For the case when single element was selected
			if (structedSelection.size() == 1) {
				
				Object selectedElement = structedSelection.getFirstElement();
				
				updateSourceFolderField(
						SelectionUtils.getHaxeSourceFolderFromSelection(
								selectedElement));
						
				updatePackageField(
						SelectionUtils.getHaxePackageFromSelection(
								selectedElement));
			}	
		}
	}

	

	/**
	 * Checks the changed dialog and validate the new state.
	 */
	@Override
	protected void dialogChangedHandler() {
		
		{
			// Source folder validate
			
			if (sourceFolder == null) {
				updateStatus("Source folder name is empty.");
				return;
			}
			
			if (!sourceFolder.getBaseFolder().isAccessible()) {
				updateStatus("Source folder doesn't exist.");
				
				EclihxUIPlugin.getLogHelper().logError(
						"Source folder doesn't exist.");
				
				return;
			}
		}
		
		{
			// Package validate			
			if (haxePackage == null) {
				updateStatus("Haxe package is empty.");
				return;
			}
			
			if (!haxePackage.getBaseFolder().isAccessible()) {
				updateStatus("Folder of the package doesn't exist.");
				
				EclihxUIPlugin.getLogHelper().logError(
						"Folder of the package doesn't exist.");
				
				return;
			}
			
			// Check that we has valid package for the current source folder.
			if (!haxePackage.getSourceFolder().equals(sourceFolder)) {
				
				// User has changed the project after setting up the package.
				boolean notifyUser = true;
				
				// If new project has the package with the same name we can
				// deal with this situation silently.
				for(IHaxePackage iterHaxePackage : sourceFolder.getPackages()) {
					if (iterHaxePackage.getName().equals(
							haxePackage.getName())) {
						haxePackage = iterHaxePackage;
						notifyUser = false;
					}
				}
				
				if (notifyUser) {
					updateStatus(
						String.format(
							"Selected project doesn't contain package '%s'.", 
							haxePackage.getName()));
					return;
				}
			}
			
		}
		
		{
			// File name validate
			String haxeFileName = getFileName();
			
			IStatus validateStatus = 
					HaxeElementValidator.validateHaxeFileName(haxeFileName);
			
						
			if (!validateStatus.isOK()) {
				updateStatus(validateStatus.getMessage());
				return;
			}
			
			if (haxePackage.hasHaxeFile(haxeFileName)) {
				updateStatus("Such Haxe file already exists.");
				return;
			}
			
		}
		
		updateStatus(null);
	}

	/**
	 * Generate text representation for the Haxe source folder.
	 * 
	 * @param folder new folder.
	 */
	private void updateSourceFolderField(IHaxeSourceFolder folder) {
		
		sourceFolder = folder;
		
		if (sourceFolder != null) {
			sourceFolderTextField.setText(String.format("%s/%s", 
					sourceFolder.getHaxeProject().getName(),
					sourceFolder.getBaseFolder().getProjectRelativePath().toString()));
			
			packageSelectButton.setEnabled(true);
			
		} else {
			sourceFolderTextField.setText("");
			packageSelectButton.setEnabled(false);
		}		
	}
	
	/**
	 * Stores the package and updates the package text field.
	 * 
	 * @param newHaxePackage new package to store.
	 */
	private void updatePackageField(IHaxePackage newHaxePackage) {
		
		haxePackage = newHaxePackage;
		
		packageTextField.setText(
				haxePackage != null ? haxePackage.getName() : "" );
	
	}

	/**
	 * Returns the entered package name.
	 * @return the package name.
	 */
	public IHaxePackage getHaxePackage() {
		return haxePackage;
	}
	
	/**
	 * Returns the entered name for the file.
	 * @return the Haxe file name.
	 */
	public String getFileName() {
		return haxeNameField.getText().endsWith(".hx") ? haxeNameField.getText() : haxeNameField.getText() + ".hx";
	}
}