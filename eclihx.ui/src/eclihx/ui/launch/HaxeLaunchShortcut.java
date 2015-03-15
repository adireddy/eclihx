package eclihx.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorPart;

import eclihx.core.haxe.model.HaxeBuildFile;
import eclihx.launching.HaxeLaunchConfigurationUtils;

/**
 * Haxe launch shortcut
 */
public class HaxeLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		// TODO: Show error		
		if (selection.isEmpty()) {
			return;
		}
		
		if (selection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) selection;
			
			if (treeSelection.size() != 1) {
				return;
			}
			
			Object firstElement = treeSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				launch((IFile) firstElement, mode);
			} else if (firstElement instanceof HaxeBuildFile) {
				launch(((HaxeBuildFile) firstElement).getBaseFile(), mode);
			}
		}
	}

	public void launch(IEditorPart editor, String mode) {
		// TODO 7 Implement this
	}
	
	protected void launch(IFile file, String mode) {
		try {
			HaxeLaunchConfigurationUtils.run(file, mode);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
