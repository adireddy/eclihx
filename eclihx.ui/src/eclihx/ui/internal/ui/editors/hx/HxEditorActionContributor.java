package eclihx.ui.internal.ui.editors.hx;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

/**
 * Haxe editor actions contributor.
 */
public class HxEditorActionContributor extends BasicTextEditorActionContributor {

	private final RetargetTextEditorAction contentAssist;
	private final RetargetTextEditorAction contentAssistTip;

	/**
	 * Default constructor.
	 */
	public HxEditorActionContributor() {
		super();
		
		contentAssist = new RetargetTextEditorAction(
				HaxeEditorMessages.getBundle(),
				HXEditor.CONTENT_PROPSALS_ACTION_RESOURCE_PREFIX);
		
		contentAssistTip = new RetargetTextEditorAction(
				HaxeEditorMessages.getBundle(),
				HXEditor.CONTENT_TIPS_ACTION_RESOURCE_PREFIX);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.BasicTextEditorActionContributor#setActiveEditor
	 * (org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		
		ITextEditor editor = null;
		if (part instanceof ITextEditor) {
			editor = (ITextEditor)part;
		}
		
		IActionBars bars = getActionBars();
        bars.setGlobalActionHandler(IHaxeEditorActionDefinitionIds.TOGGLE_COMMENT, 
        		getAction(editor, HXEditor.TOGGLE_COMMENT_ACTION_ID));
		
		contentAssist.setAction(getAction(editor, HXEditor.CONTENT_PROPSALS_ACTION_ID));
		contentAssistTip.setAction(getAction(editor, HXEditor.CONTENT_TIPS_ACTION_ID));
	}
}
