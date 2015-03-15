package eclihx.launching;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

/**
 * Compute the path for the Haxe elements.
 * 
 * TODO 5: Finish work on this class. 
 */
public class HaxeSourcePathComputerDelegate 
		implements ISourcePathComputerDelegate {
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {

		String path = configuration.getAttribute(
				IHaxeLaunchConfigurationConstants.PROJECT_NAME, (String) null);
		
		ISourceContainer sourceContainer = null;
		
		if (path != null) {
			IResource resource = 
					ResourcesPlugin.getWorkspace().getRoot().findMember(
							new Path(path));
			
			if (resource != null) {
				IContainer container = resource.getParent();
				
				if (container.getType() == IResource.PROJECT) {
					sourceContainer = 
							new ProjectSourceContainer(
									(IProject) container, false);
				} else if (container.getType() == IResource.FOLDER) {
					sourceContainer = 
							new FolderSourceContainer(container, false);
				}
			}
		}
		if (sourceContainer == null) {
			sourceContainer = new WorkspaceSourceContainer();
		}
		
		return new ISourceContainer[] { sourceContainer };
	}
}
