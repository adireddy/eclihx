package eclihx.ui.internal.ui.editors.hx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import eclihx.ui.actions.ToggleCommentAction;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.editors.ColorManager;

/**
 * Class extend functionality of the standard text editor to make it work with
 * Haxe language source.
 */
public class HXEditor extends TextEditor {
	
	/**
	 * Content assist proposals action identifier.
	 */
	public static final String CONTENT_PROPSALS_ACTION_ID = "ContentAssistProposals";
	
	/**
	 * Content assist proposal action prefix in resource bundle resources.
	 */
	public static final String CONTENT_PROPSALS_ACTION_RESOURCE_PREFIX = "ContentAssistProposals.";
	
	/**
	 * Content assist tips action identifier.
	 */
	public static final String CONTENT_TIPS_ACTION_ID = "ContentAssistTips.";
	
	/**
	 * Content assist tips action prefix in resource bundle resources.
	 */
	public static final String CONTENT_TIPS_ACTION_RESOURCE_PREFIX = "ContentAssistTips";
	
	/**
	 * Unique id for the editor scope for toggle comment action.
	 */
	public static final String TOGGLE_COMMENT_ACTION_ID = "org.eclihx.uiToggleCommentAction";
	
	/**
	 * Prefix to be prepended to the various resource keys for toggle comment action.
	 */
	public static final String TOGGLE_COMMENT_ACTION_PREFIX = "ToggleCommentAction";
	
	private IContentOutlinePage haxeOutlinePage;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions() {
		
		super.createActions();
		
		IAction assistProposalsAction = new TextOperationAction(
				HaxeEditorMessages.getBundle(), 
				CONTENT_PROPSALS_ACTION_RESOURCE_PREFIX, 
				this,
				ISourceViewer.CONTENTASSIST_PROPOSALS);		
		assistProposalsAction.setActionDefinitionId(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);		
		setAction(CONTENT_PROPSALS_ACTION_ID, assistProposalsAction);
		
		IAction assistContextTipsAction = new TextOperationAction(
				HaxeEditorMessages.getBundle(), 
				CONTENT_TIPS_ACTION_RESOURCE_PREFIX, 
				this,
				ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);		
		assistContextTipsAction.setActionDefinitionId(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);		
		setAction(CONTENT_TIPS_ACTION_ID, assistContextTipsAction);		
		
		ToggleCommentAction toggleCommentAction = new ToggleCommentAction(
				HaxeEditorMessages.getBundle(), 
				TOGGLE_COMMENT_ACTION_PREFIX, 
				this);
		toggleCommentAction.setActionDefinitionId(IHaxeEditorActionDefinitionIds.TOGGLE_COMMENT);
		toggleCommentAction.configure(getSourceViewer(), getSourceViewerConfiguration());
		setAction(TOGGLE_COMMENT_ACTION_ID, toggleCommentAction);		
	}
	
	/**
	 * Color manager.
	 */
	private final ColorManager colorManager;

	/**
	 * Combine eclihx.ui preference store with EditorUI store.
	 * 
	 * @return combined preference store.
	 */
	private IPreferenceStore createCombinedPreferenceStore() {
		List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(2);

		stores.add(EclihxUIPlugin.getDefault().getPreferenceStore());
		stores.add(EditorsUI.getPreferenceStore());

		return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
	}

	/**
	 * Standard constructor.
	 */
	public HXEditor() {
		super();

		setPreferenceStore(createCombinedPreferenceStore());
		setDocumentProvider(new HXDocumentProvider());

		colorManager = new ColorManager();
		setSourceViewerConfiguration(new HXSourceViewerConfiguration(
				colorManager));
		
		setEditorContextMenuId("#HaxeEditorContext"); //$NON-NLS-1$
	}	
	
	/*
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeKeyBindingScopes()
	 */
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "eclihx.ui.haxeEditorScope" }); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#affectsTextPresentation(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {

		if (getSourceViewerConfiguration() instanceof HXSourceViewerConfiguration) {
			((HXSourceViewerConfiguration) getSourceViewerConfiguration()).adaptToPreferenceChange(event);
			return true;
		}

		return false;
	}
	
//	@Override
//	protected void editorSaved() {
//	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (haxeOutlinePage == null) {
				haxeOutlinePage = new HaxeOutlinePage(this);
			}
			return haxeOutlinePage;
		}
		return super.getAdapter(required);
	}
}
