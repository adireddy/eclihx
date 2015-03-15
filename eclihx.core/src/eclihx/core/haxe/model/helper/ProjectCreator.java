package eclihx.core.haxe.model.helper;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.InvalidBuildFileNameException;
import eclihx.core.haxe.model.core.IHaxeProject;

/**
 * Special class for creating different project Haxe-projects.
 */
public class ProjectCreator {
	
	private static String getConcatenatedPath(String parentPath, String childPath) {
		return (new File(parentPath, childPath)).getPath();
	}
	
	private static String createDefaultHxmlContent(String projectName, 
			String outFolderName, String srcFolderName) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("-cp " + srcFolderName + "\n");
		builder.append("\n");
		
		builder.append("# Uncomment the desirable target\n");
		
		builder.append("# JavaScript target\n");
		builder.append(String.format("# -js %s\n", getConcatenatedPath(outFolderName, projectName + ".js")));
		builder.append("\n");

		builder.append("# SWF 9 target\n");
		builder.append(String.format("# -swf9 %s\n", getConcatenatedPath(outFolderName, projectName + ".swf")));
		builder.append("\n");
		
		builder.append("# ActionScript3 target\n");
		builder.append(String.format("# -as3 %s\n", outFolderName));
		builder.append("\n");

		builder.append("# Neko target\n");
		builder.append(String.format("# -neko %s\n", getConcatenatedPath(outFolderName, projectName + ".n")));
		builder.append("\n");
		
		builder.append("# PHP target\n");
		builder.append(String.format("# -php %s\n", outFolderName));
		builder.append("\n");
		
		builder.append("# C++ target\n");
		builder.append(String.format("# -cpp %s\n", outFolderName));
		builder.append("\n");
				
		builder.append("# Uncomment and place your main class with package\n");
		builder.append("# -main package.subpackage.ClassName\n");		
		
		return builder.toString();
	}
	
	/**
	 * Create a simple project.
	 * @param projectName Name of the created project. Can't be null.
	 * @param buildFileName Default build file name. Can't be null.
	 * @param outFolderName Output folder name. Can't be null.
	 * @param srcFolderName Source folder name. Can't be null.
	 * @param monitor UI progress monitor. Can't be null.
	 * @throws CoreException some project creation exception.
	 */
	public static void createCommonProject(String projectName, 
			String buildFileName, String outFolderName, String srcFolderName, 
			IProgressMonitor monitor) throws CoreException  {
		
		monitor.beginTask("Simple Haxe project creation", 4);
		
		monitor.setTaskName("Creation of project: " + projectName);		
		IHaxeProject haxeProject = EclihxCore.getDefault().getHaxeWorkspace().
				createHaxeProject(projectName, monitor);		
		monitor.worked(1);
		
		monitor.setTaskName("Creation of output folder: " + outFolderName);		
		haxeProject.createOutputFolder(outFolderName, monitor);		
		monitor.worked(1);
		
		monitor.setTaskName("Creation of source folder: " + srcFolderName);		
		haxeProject.createSourceFolder(srcFolderName, monitor);		
		monitor.worked(1);
		
		monitor.setTaskName("Creation of build file: " + buildFileName);		
		haxeProject.createBuildFile(buildFileName, 
				createDefaultHxmlContent(projectName, outFolderName, srcFolderName), 
				monitor);
		
		IFile[] buildFiles = haxeProject.getBuildFiles();
		if (buildFiles.length != 0) {
			try {
				haxeProject.setContentAssistBuildFile(buildFiles[0].getLocation().toOSString());
			} catch (InvalidBuildFileNameException e) {
				// TODO 5: Notify user about errors
				EclihxCore.getLogHelper().logError(e);
			}
		}
		
		monitor.worked(1);
	}
}
