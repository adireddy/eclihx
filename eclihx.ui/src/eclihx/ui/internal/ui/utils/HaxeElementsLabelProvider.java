package eclihx.ui.internal.ui.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import eclihx.core.haxe.model.core.IHaxeBuildFile;
import eclihx.core.haxe.model.core.IHaxeElement;
import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFile;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.ui.PluginImages;

/**
 * Get the elements view description.
 * 
 * TODO 6 Make it work not only for the IHaxeProject and IHaxeSourceFolder
 */
public class HaxeElementsLabelProvider extends LabelProvider {

	/**
	 * Standard label provider for showing non-specific Haxe elements.
	 */
	private final WorkbenchLabelProvider workcbenchProvider;
	
	/**
	 * Default constructor. 
	 */
	public HaxeElementsLabelProvider() {
		workcbenchProvider = new WorkbenchLabelProvider();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IHaxeElement) {
			return ((IHaxeElement)element).getName();
		}
		
		if (element instanceof IResource) {
			return workcbenchProvider.getText(element);
		}

		return super.getText(element);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IHaxeProject) {
			return PluginImages.get(PluginImages.IMG_PROJECT);
		} 
		
		if (element instanceof IHaxeSourceFolder) {
			return PluginImages.get(PluginImages.IMG_SOURCE_FOLDER);
		} 
		
		if (element instanceof IHaxePackage) {
			return PluginImages.get(PluginImages.IMG_PACKAGE);
		}
		
		if (element instanceof IHaxeOutputFolder) {
			// TODO 4 add new image for the output folder
			return workcbenchProvider.getImage(
					((IHaxeOutputFolder)element).getBaseFolder());
		}
		
		if (element instanceof IHaxeSourceFile) {
			return workcbenchProvider.getImage(
					((IHaxeSourceFile)element).getBaseResource());
		}
		
		if (element instanceof IHaxeBuildFile) {
			return workcbenchProvider.getImage(
					((IHaxeBuildFile)element).getBaseResource());
		}
		
		if (element instanceof IResource) {
			return workcbenchProvider.getImage(element);
		}	
		
		return super.getImage(element);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		workcbenchProvider.dispose();
		super.dispose();
	}
}
