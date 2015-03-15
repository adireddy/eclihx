package eclihx.ui.internal.ui.editors.hx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.templates.ITemplatesPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import eclihx.ui.PreferenceConstants;
import eclihx.ui.actions.FormatAllAction;
import eclihx.ui.actions.ToggleCommentAction;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.editors.ColorManager;
import eclihx.ui.internal.ui.editors.extensions.bracketinserter.Filter;
import eclihx.ui.internal.ui.editors.extensions.bracketinserter.GenericBracketInserter;
import eclihx.ui.internal.ui.editors.templates.HaxeTemplatesPage;

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
	private ITemplatesPage templatesPage;
	
	// private BracketInserter bracketInserter = new BracketInserter(EclihxUIPlugin.getLogHelper());
	private GenericBracketInserter bracketInserter = new GenericBracketInserter();
	
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
		setSourceViewerConfiguration(new HXSourceViewerConfiguration(colorManager));
		
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
	
	@Override
	public void createPartControl(Composite parent) {	
		
		super.createPartControl(parent);
		
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension) {
			configureBracketInserter(sourceViewer);

			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(bracketInserter);
		}
	}
	
	private void configureBracketInserter(ISourceViewer sourceViewer) {
		bracketInserter.setViewer(sourceViewer);
		
		Filter<String> codeFilter = Filter.equal(IDocument.DEFAULT_CONTENT_TYPE).or(Filter.equal(IHXPartitions.HX_PREPROCESSOR));
		
		bracketInserter.addBrackets('(', ')', codeFilter);		
		bracketInserter.addBrackets('<', '>', codeFilter);
		bracketInserter.addBrackets('[', ']', codeFilter);
		bracketInserter.addBrackets('{', '}', codeFilter);
		
		bracketInserter.addBrackets("double_quotes_insert_rule", 
				new GenericBracketInserter.PairConfiguration('"', '"', 
						codeFilter, 
						codeFilter.or(Filter.equal(IHXPartitions.HX_STRING))));
		
		bracketInserter.addBrackets("single_quotes_insert_rule",
				new GenericBracketInserter.PairConfiguration('\'', '\'', 
						codeFilter, 
						codeFilter.or(Filter.equal(IHXPartitions.HX_STRING))));
	}
	
	/**
	 * @return Editor source viewer.
	 */
	public ISourceViewer getViewer() {
		return super.getSourceViewer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	@Override
	public void dispose() {
		colorManager.dispose();
		
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) sourceViewer).removeVerifyKeyListener(bracketInserter);
		}
		
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
		
		if (ITemplatesPage.class.equals(required)) {
			if (templatesPage == null) {
				templatesPage = new HaxeTemplatesPage(this);
			}
			
			return templatesPage;
		}
		
		return super.getAdapter(required);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		
		final String property= event.getProperty();

		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(property) ||
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(property)) {
			return;
		}
		
		if (PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS.equals(property) || 
				PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH.equals(property)) {
			uninstallTabsToSpacesConverter();
			
			if (isTabsToSpacesConversionEnabled()) {
				// Reinstall standard space converter
				installTabsToSpacesConverter();
			} else {
				StyledText textWidget = getSourceViewer().getTextWidget();
				int tabWidth = getSourceViewerConfiguration().getTabWidth(getSourceViewer());
				if (textWidget.getTabs() != tabWidth) {
					textWidget.setTabs(tabWidth);
				}
			}
		}
		
		super.handlePreferenceStoreChanged(event);
	}
	
	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(new DefaultCharacterPairMatcher(
				new char[] { '(', ')', '<', '>', '[', ']', '{', '}' }));
		
		support.setMatchingCharacterPainterPreferenceKeys(
				PreferenceConstants.HX_EDITOR_MATCHING_BRACKETS, 
				PreferenceConstants.HX_EDITOR_MATCHING_BRACKETS_COLOR);
		
		super.configureSourceViewerDecorationSupport(support);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isTabsToSpacesConversionEnabled()
	 */
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return !FormatAllAction.getPreferenceOptions().isInsertTabs();
	}	
}
