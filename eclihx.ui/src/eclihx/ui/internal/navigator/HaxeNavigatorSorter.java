package eclihx.ui.internal.navigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import eclihx.core.haxe.model.core.IHaxeBuildFile;
import eclihx.core.haxe.model.core.IHaxeElement;
import eclihx.core.haxe.model.core.IHaxeOutputFolder;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;

/**
 * Sorts elements in Haxe element navigator. 
 */
public class HaxeNavigatorSorter extends ViewerSorter {

	private static final int PROJECTS = 1;
	private static final int OUTPUT_FOLDER = 2;
	private static final int SOURCE_FOLDER = 3;
	private static final int BUILD_FILES = 4;
	
	private static final int OTHERS = 50;
	
	/**
	 * Some request will be given to the standard comparator.
	 */
	//private final ResourceComparator fResourceComparator;
	
	/**
	 * Default constructor.
	 */
	public HaxeNavigatorSorter() {
		//fResourceComparator = new ResourceComparator(ResourceComparator.NAME); 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return super.compare(viewer, e1, e2);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	@Override
	public int category(Object element) {
		if (element instanceof IHaxeElement) {
			if (element instanceof IHaxeProject) {
				return PROJECTS;
			} else if (element instanceof IHaxeOutputFolder) {
				return OUTPUT_FOLDER;
			} else if (element instanceof IHaxeSourceFolder) {
				return SOURCE_FOLDER;
			} else if (element instanceof IHaxeBuildFile) {
				return BUILD_FILES;
			}				
		}
		
		return OTHERS;
	}

}
