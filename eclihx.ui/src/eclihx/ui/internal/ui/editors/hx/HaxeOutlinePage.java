package eclihx.ui.internal.ui.editors.hx;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page for haxe code file.
 */
public class HaxeOutlinePage extends ContentOutlinePage {
	
	private HXEditor hxEditor;
	
	/**
	 * Default constructor. 
	 * 
	 * @param hxEditor an haxe editor this outline view is constructing.
	 */
	public HaxeOutlinePage(HXEditor hxEditor) {
		super();
		this.hxEditor = hxEditor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new OutlineContentProvider());
		viewer.setLabelProvider(new OutlineLabelProvider());
		viewer.addSelectionChangedListener(this);
		viewer.setInput(hxEditor);
	}	
}
