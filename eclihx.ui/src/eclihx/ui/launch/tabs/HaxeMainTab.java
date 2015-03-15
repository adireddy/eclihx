package eclihx.ui.launch.tabs;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;

import eclihx.core.CorePreferenceInitializer;
import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.launching.IHaxeLaunchConfigurationConstants;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.StandardDialogs;

/**
 * This class contains functionality for the main launch configuration tab
 */
public final class HaxeMainTab extends AbstractLaunchConfigurationTab {

	// TODO 3 move strings constants to configure file

	/**
	 * If this variable is <code>null</code> it's mean that we hadn't found the
	 * project.
	 */
	IHaxeProject haxeProject;
	
	/**
	 * Here we are going to store all build files in the project.
	 * This variable isn't supposed to get null values.
	 */
	final ArrayList<IFile> buildFilesCache = new ArrayList<IFile>();

	/* Controls */
	private Text projectNameText;
	private Text buildFileNameText;
	private Text workingDirectoryText;
	private Button projectBuildButton;

	private final ModifyListener fModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent modifyEvent) {
			updateLaunchConfigurationDialog();
		}
	};

	/**
	 * Handler for the button of project selection.
	 * 
	 * @param event Selection event arguments.
	 */
	protected void onProjectButtonSelected(SelectionEvent event) {

		AbstractElementListSelectionDialog dialog = 
			StandardDialogs.createHaxeProjectsNamesDialog(
					getShell(), projectNameText.getText()); 
			
		if (dialog.open() == Window.OK) {
			String projectNewName = dialog.getFirstResult().toString();

			projectNameText.setText(projectNewName);

			// After that current project should be updated
			if (haxeProject != null) {
				if (!buildFilesCache.isEmpty()) {
					buildFileNameText.setText(buildFilesCache.get(0).getLocation().toOSString());
					workingDirectoryText.setText(buildFilesCache.get(0).getParent().getLocation().toOSString());
				} else {
					buildFileNameText.setText("");
					workingDirectoryText.setText("");					
				}
			}
		}
	}

	/**
	 * Handler for the button of project target file selector
	 * 
	 * @param event
	 */
	protected void onBuildFileSelected(SelectionEvent event) {
		
		AbstractElementListSelectionDialog dialog =
			StandardDialogs.createBuildFilesDialog(
					getShell(), haxeProject, buildFileNameText.getText());

		if (dialog.open() == Window.OK) {
			String buildFileName = dialog.getFirstResult().toString();

			if (buildFileName != null) {
				buildFileNameText.setText(buildFileName);

				for (IFile buildFile : buildFilesCache) {
					if (buildFile.getLocation().toOSString().equals(buildFileName)) {
						workingDirectoryText.setText(buildFile.getParent().getLocation().toOSString());
					}	
				}
			} else {
				EclihxUIPlugin.getLogHelper().logInfo("The selected file wasn't found.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		// TODO 2 Add image for the main page of the launcher configuration
		return super.getImage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);

		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout());

		GridData horizontantalGrid = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		GridLayout layout = new GridLayout(1, false);

		// Add project group
		Group projectGroup = new Group(top, SWT.NONE);
		projectGroup.setText("Project");
		projectGroup.setLayout(new GridLayout(2, false));
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		projectNameText = new Text(projectGroup, SWT.BORDER);
		projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectNameText.setEditable(false); // Do not allow modification.
		projectNameText.setText("");
		projectNameText.addModifyListener(fModifyListener);
		projectNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				updateCurrentProject(projectNameText.getText());
			}
		});

		Button projectButton = createPushButton(projectGroup,
				"Select Project...", null);
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onProjectButtonSelected(e);
			}
		});

		// Add build file group
		Group buildFileGroup = new Group(top, SWT.NONE);
		buildFileGroup.setText("Build file");
		buildFileGroup.setLayout(new GridLayout(2, false));
		buildFileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buildFileNameText = new Text(buildFileGroup, SWT.BORDER);
		buildFileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buildFileNameText.setEditable(false);
		buildFileNameText.setText("");
		buildFileNameText.addModifyListener(fModifyListener);

		projectBuildButton = createPushButton(buildFileGroup, "Build file...", 
				null);
		projectBuildButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				onBuildFileSelected(event);
			}
		});

		// Add working  directory
		Group workingDirectoryGroup = new Group(top, SWT.NONE);
		workingDirectoryGroup.setText("Execute from directory");
		workingDirectoryGroup.setLayout(layout);
		workingDirectoryGroup.setLayoutData(horizontantalGrid);

		workingDirectoryText = new Text(workingDirectoryGroup, SWT.BORDER);
		workingDirectoryText.setLayoutData(horizontantalGrid);
		workingDirectoryText.setEditable(false);
		workingDirectoryText.setText("");

		setControl(top);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Main";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		initializeProjectName(configuration);
		initializeBuildFile(configuration);
		initializeWorkingDirectory(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
	 * .debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.PROJECT_NAME, projectNameText.getText());
		
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.BUILD_FILE, buildFileNameText.getText());
		
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.WORKING_DIRECTORY, workingDirectoryText.getText());
		
		// TODO 3 Make separate place for overriding initializer
		configuration.setAttribute(
			IHaxeLaunchConfigurationConstants.HAXE_COMPILER_PATH,
			EclihxCore.getDefault().getPluginPreferences().getString(
				CorePreferenceInitializer.HAXE_COMPILER_PATH
			)
		);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.PROJECT_NAME, "");
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.BUILD_FILE, "");
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.WORKING_DIRECTORY, "");
		configuration.setAttribute(
				IHaxeLaunchConfigurationConstants.HAXE_COMPILER_PATH, "");
	}

	/**
	 * Initialize project name field from the configuration
	 */
	private void initializeProjectName(ILaunchConfiguration configuration) {
		String projectNameString = "";
		try {
			
			projectNameString = configuration.getAttribute(
					IHaxeLaunchConfigurationConstants.PROJECT_NAME, projectNameString);
			
		} catch (CoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}

		projectNameText.setText(projectNameString);
	}

	/**
	 * Initialize working directory field from the configuration
	 */
	private void initializeWorkingDirectory(ILaunchConfiguration configuration) {
		String workingDirectoryString = "";
		
		try {
			workingDirectoryString = configuration.getAttribute(
					IHaxeLaunchConfigurationConstants.WORKING_DIRECTORY,
					workingDirectoryString);
		} catch (CoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}
		
		workingDirectoryText.setText(workingDirectoryString);
	}

	/**
	 * Initialize build file field from the configuration
	 */
	private void initializeBuildFile(ILaunchConfiguration configuration) {
		String buildFileString = "";
		try {
			buildFileString = configuration.getAttribute(
					IHaxeLaunchConfigurationConstants.BUILD_FILE,
					buildFileString);
		} catch (CoreException e) {
			EclihxCore.getLogHelper().logError(e);
		}
		buildFileNameText.setText(buildFileString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {

		// TODO 7 Make separate validator for configuration

		if (!super.isValid(launchConfig))
			return false;

		try {

			// Project name
			String projectName = launchConfig.getAttribute(
					IHaxeLaunchConfigurationConstants.PROJECT_NAME, "");
			
			if (projectName == null || projectName.isEmpty()) {
				setMessage("Choose the Haxe project");
				setErrorMessage(null);
				return false;
			}

			// Build file check
			if (buildFilesCache != null && buildFilesCache.isEmpty()) {
				setErrorMessage(
					"This version of Eclihx only supports building based " +
					"on hxml-file. Please create one for this project before " +
					"proceeding with launching");
				setMessage(null);
				return false;
			}

			String buildFilePath = launchConfig.getAttribute(
					IHaxeLaunchConfigurationConstants.BUILD_FILE, "");
			if (buildFilePath.isEmpty()) {
				setMessage("Please, select build file for this project.");
				setErrorMessage(null);
				return false;
			}

			// compiler check
			String executablePath = launchConfig.getAttribute(
					IHaxeLaunchConfigurationConstants.HAXE_COMPILER_PATH, "");
			
			if (executablePath.isEmpty()) {
				setErrorMessage(
					"Please, define Haxe compiler first " +
					"(Preferences->Eclihx->Compiler).");
				setMessage(null);
				return false;
			}

			if (haxeProject == null) {
				setErrorMessage("There is no project with given name.");
				setMessage(null);
				return false;
			}

		} catch (CoreException e) {
			return false;
		}

		// Everything is OK
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/**
	 * Update local variables after changing of the selected project.
	 * @param projectName new project name.
	 */
	private void updateCurrentProject(String projectName) {
		try {
			
			buildFilesCache.clear();
			projectBuildButton.setEnabled(false);
			
			if (projectName.isEmpty()) {
				haxeProject = null;
			} else {
				haxeProject = EclihxCore.getDefault().getHaxeWorkspace().getHaxeProject(projectName);
			}

			if (haxeProject != null) {				
				if (!haxeProject.isOpen()) {
					haxeProject.open(null);
				}
				
				buildFilesCache.addAll(Arrays.asList(haxeProject.getBuildFiles()));
				projectBuildButton.setEnabled(true);
			} 
		} catch (CoreException e) {
			haxeProject = null;
			buildFilesCache.clear();
		}		
	}
}
