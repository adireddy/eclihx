package eclihx.ui.internal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.ui.PluginImages;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.HaxeElementFilter.ShowElement;

/**
 * Initialize some dialogs like project selection dialog and others.
 */
public final class StandardDialogs {

	/**
	 * Get a project selector dialog.
	 * 
	 * @param shell the parent shell of the dialog.
	 * @param selectedProject the name of the originally selected project.
	 * 
	 * @return dialog with the Haxe projects names list.
	 */
	public static AbstractElementListSelectionDialog createHaxeProjectsNamesDialog(
			Shell shell, String selectedProject) {

		ILabelProvider labelProvider = new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PluginImages.get(PluginImages.IMG_PROJECT);
			}
		};

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, labelProvider);

		// Messages
		dialog.setTitle("Select Haxe Project");
		dialog.setMessage("Enter a string to search for a project:");
		dialog
				.setEmptyListMessage("There are no Haxe projects. You should create one first.");

		// Initialize the list of elements
		String[] projectNames = EclihxCore.getDefault().getHaxeWorkspace()
				.getHaxeProjectsNames();

		dialog.setElements(projectNames);
		dialog.setInitialSelections(new Object[] { selectedProject });

		return dialog;
	}

	/**
	 * Create a Haxe build file dialog
	 * 
	 * @param shell the parent shell of the dialog.
	 * @param haxeProject project which build files should be listed.
	 * @param buildFileSelection the name of the originally selected file.
	 * 
	 * @return dialog with the list of the build files for the selected project.
	 */
	public static AbstractElementListSelectionDialog createBuildFilesDialog(
			Shell shell, IHaxeProject haxeProject, String buildFileSelection) {

		ILabelProvider labelProvider = new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return PluginImages.get(PluginImages.IMG_BUILD_FILE);
			}
		};

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, labelProvider);

		// Messages
		dialog.setTitle("Select Haxe Build File");
		dialog.setMessage("Enter a string to search for a file:");
		dialog.setEmptyListMessage("The project has no build files. "
				+ "Please, create one before to proceed.");

		// Initialize

		try {

			IFile[] paths = haxeProject.getBuildFiles();

			// FIXME 2 Move to separate method
			List<String> stringPaths = new ArrayList<String>(paths.length);
			for (IFile file : paths) {
				stringPaths.add(file.getLocation().toOSString());
			}

			dialog.setElements(stringPaths.toArray(new String[0]));
			dialog.setInitialSelections(new Object[] { buildFileSelection });

		} catch (CoreException e) {
			EclihxUIPlugin.getLogHelper().logError(e);
		}

		return dialog;
	}

	/**
	 * Creates standard child dialog where user can select the particular
	 * project.
	 * 
	 * This method should be used for workspace relative selection dialog.
	 * 
	 * @param shell the parent shell of the dialog.
	 * @return returns dialog with the list of the Haxe projects in workspace.
	 */
	public static ElementTreeSelectionDialog createHaxeProjectsDialog(
			Shell shell) {

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new HaxeElementsLabelProvider(),
				new HaxeElementsContentProvider(new HaxeElementFilter(
						ShowElement.Project)));

		dialog.setTitle("Haxe Project");
		dialog.setMessage("Choose a Haxe project:");
		dialog.setHelpAvailable(false);

		// Sets Haxe workspace as a root of the resource tree
		dialog.setInput(EclihxCore.getDefault().getHaxeWorkspace());

		return dialog;
	}

	/**
	 * Creates standard child dialog where user can select the particular Haxe
	 * source folder.
	 * 
	 * This method should be used for workspace relative selection dialog.
	 * 
	 * @param shell the parent shell of the dialog.
	 * @return returns dialog with the list of the Haxe source folders.
	 */
	public static ElementTreeSelectionDialog createHaxeSourceFoldersDialog(
			Shell shell) {

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				shell, new HaxeElementsLabelProvider(),
				new HaxeElementsContentProvider(new HaxeElementFilter(
						ShowElement.SourceFolder)));

		dialog.setTitle("Source Folder Selection");
		dialog.setMessage("Choose a source folder:");
		dialog.setHelpAvailable(false);

		// Sets Haxe workspace as a root of the resource tree
		dialog.setInput(EclihxCore.getDefault().getHaxeWorkspace());

		// Source folder validator
		// TODO 3 Move the class definition to separate place.
		dialog.setValidator(new ISelectionStatusValidator() {

			private final Status errorStatus = new Status(IStatus.ERROR,
					EclihxUIPlugin.PLUGIN_ID, "");

			private final Status okStatus = new Status(IStatus.OK,
					EclihxUIPlugin.PLUGIN_ID, "");

			@Override
			public IStatus validate(Object[] selection) {
				if (selection.length == 1
						&& selection[0] instanceof IHaxeSourceFolder) {
					return okStatus;
				}

				return errorStatus;
			}

		});

		return dialog;

	}

	/**
	 * Creates standard child dialog where user can select the particular Haxe
	 * source folder.
	 * 
	 * This folder shows only source folders in the specified Haxe project.
	 * 
	 * @param shell the parent shell of the dialog.
	 * @param project the project which source folders should be shown.
	 * 
	 * @return returns dialog with the list of the Haxe source folders relative
	 *         the the specified project.
	 */
	public static ElementTreeSelectionDialog createHaxeSourceFoldersDialog(
			Shell shell, IHaxeProject project) {

		if (project == null) {
			throw new NullPointerException("Project parameter can't be null");
		}

		ElementTreeSelectionDialog dialog = createHaxeSourceFoldersDialog(shell);

		// Redefine root element
		dialog.setInput(project);

		return dialog;
	}

	/**
	 * Creates standard child dialog where user can select the particular Haxe
	 * package.
	 * 
	 * This folder shows only source folders in the specified Haxe project.
	 * 
	 * @param shell the parent shell of the dialog.
	 * @param sourceFolder Haxe source folder with packages.
	 * @return the dialog with the list of the packages.
	 */
	public static ElementListSelectionDialog createHaxePackagesDialog(
			Shell shell, IHaxeSourceFolder sourceFolder) {
		
		IHaxePackage[] packages;
		
		if (sourceFolder != null) {
			packages = sourceFolder.getPackages();
		} else {
			packages = new IHaxePackage[0];
		}
		
		ElementListSelectionDialog dialog = 
			new ElementListSelectionDialog(
					shell, 
					new HaxeElementsLabelProvider());
	
		dialog.setIgnoreCase(false);
		dialog.setTitle("Haxe Package Selection"); 
		dialog.setMessage("Select a package:"); 
		dialog.setEmptyListMessage("There are no packages in this source folder"); 
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);
		
		return dialog;
	}
	
}
