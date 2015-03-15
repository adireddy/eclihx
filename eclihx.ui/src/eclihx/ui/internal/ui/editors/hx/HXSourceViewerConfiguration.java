/* Seems should be deleted */

package eclihx.ui.internal.ui.editors.hx;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.editors.AbstractScanner;
import eclihx.ui.internal.ui.editors.ColorManager;
import eclihx.ui.internal.ui.editors.SingleTokenScanner;

/**
 * This class bundles the configuration space of a source viewer. Instances of 
 * this class are passed to the configure method of ISourceViewer. Each method 
 * in this class get as argument the source viewer for which it should provide a 
 * particular configuration setting such as a presentation reconciler. Based on 
 * its specific knowledge about the returned object, the configuration might 
 * share such objects or compute them according to some rules.
 */
public class HXSourceViewerConfiguration extends TextSourceViewerConfiguration {
	
	private ITextDoubleClickStrategy doubleClickStrategy;
	
	private final HXScanner hxCodeScanner;
	private final AbstractScanner singleLineCommentScanner;
	private final AbstractScanner multiLineCommentScanner;
	private final AbstractScanner stringScanner;
	private final AbstractScanner regexprScanner;
	private final AbstractScanner hxDocScanner;
	private final AbstractScanner hxConditionCompilationScanner;
	
	/**
	 * Distribute property change events to all interested parts.
	 * 
	 * @param event an event to distribute.
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		hxCodeScanner.adaptToPreferenceChange(event);
		singleLineCommentScanner.adaptToPreferenceChange(event);
		multiLineCommentScanner.adaptToPreferenceChange(event);
		stringScanner.adaptToPreferenceChange(event);
		regexprScanner.adaptToPreferenceChange(event);
		hxDocScanner.adaptToPreferenceChange(event);
		hxConditionCompilationScanner.adaptToPreferenceChange(event);
	}
	
	/**
	 * Method for configuring content assistant in Haxe code. 
	 */
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		
		HXContextAssist haxeContextAssist = new HXContextAssist();
		
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(300);
		assistant.setContentAssistProcessor(haxeContextAssist, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.addCompletionListener(haxeContextAssist);
		assistant.setShowEmptyList(true);
		
		return assistant;
	}

	/**
	 * Configuration constructor. 
	 * @param colorManager A manager for the used colors.
	 */
	public HXSourceViewerConfiguration(ColorManager colorManager) {
		
		IPreferenceStore store = EclihxUIPlugin.getDefault().getPreferenceStore();
		hxCodeScanner = new HXScanner(colorManager, store);
		
		singleLineCommentScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_COMMENT_COLOR, 
			PreferenceConstants.HX_EDITOR_COMMENT_BOLD, 
			PreferenceConstants.HX_EDITOR_COMMENT_ITALIC);
		
		multiLineCommentScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_COLOR, 
			PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_BOLD, 
			PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_ITALIC);
		
		stringScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_STRING_COLOR, 
			PreferenceConstants.HX_EDITOR_STRING_BOLD, 
			PreferenceConstants.HX_EDITOR_STRING_ITALIC);
		
		regexprScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_REGEXPR_COLOR, 
			PreferenceConstants.HX_EDITOR_REGEXPR_BOLD, 
			PreferenceConstants.HX_EDITOR_REGEXPR_ITALIC);
		
		hxDocScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_HAXE_DOC_COLOR, 
			PreferenceConstants.HX_EDITOR_HAXE_DOC_BOLD, 
			PreferenceConstants.HX_EDITOR_HAXE_DOC_ITALIC);
		
		hxConditionCompilationScanner = new SingleTokenScanner(
			colorManager, store, 
			PreferenceConstants.HX_EDITOR_CONDITIONAL_COMPILATION_COLOR, 
			PreferenceConstants.HX_EDITOR_CONDITIONAL_COMPILATION_BOLD, 
			PreferenceConstants.HX_EDITOR_CONDITIONAL_COMPILATION_ITALIC);
	}
	
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			IHXPartitions.HX_SINGLE_LINE_COMMENT,
			IHXPartitions.HX_MULTI_LINE_COMMENT,
			IHXPartitions.HX_STRING,
			IHXPartitions.HX_REGEXPR,
			IHXPartitions.HX_PREPROCESSOR
		};
	}
	
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
		{
			doubleClickStrategy = new HXDoubleClickStrategy();
		}
		
		return doubleClickStrategy;
	}
	

	protected HXScanner getHXScanner() {
		return hxCodeScanner;
	}
	
	
	protected AbstractScanner getSingleLineCommentScanner() {
		return singleLineCommentScanner;
	}
	
	
	protected AbstractScanner getMultiLineCommentScanner() {
		return multiLineCommentScanner;
	}
	
	
	protected AbstractScanner getStringScanner() {
		return stringScanner;
	}
	
	
	protected AbstractScanner getRegexprScanner() {
		return regexprScanner;
	}
	
	protected AbstractScanner getHXConditionCompilationScanner() {
		return hxConditionCompilationScanner;
	}
	

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getHXScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getSingleLineCommentScanner());
		reconciler.setDamager(dr, IHXPartitions.HX_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IHXPartitions.HX_SINGLE_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getMultiLineCommentScanner());
		reconciler.setDamager(dr, IHXPartitions.HX_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IHXPartitions.HX_MULTI_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IHXPartitions.HX_STRING);
		reconciler.setRepairer(dr, IHXPartitions.HX_STRING);
		
		dr = new DefaultDamagerRepairer(getRegexprScanner());
		reconciler.setDamager(dr, IHXPartitions.HX_REGEXPR);
		reconciler.setRepairer(dr, IHXPartitions.HX_REGEXPR);
		
		dr = new DefaultDamagerRepairer(hxDocScanner);
		reconciler.setDamager(dr, IHXPartitions.HX_DOC);
		reconciler.setRepairer(dr, IHXPartitions.HX_DOC);
		
		dr = new DefaultDamagerRepairer(getHXConditionCompilationScanner());
		reconciler.setDamager(dr, IHXPartitions.HX_PREPROCESSOR);
		reconciler.setRepairer(dr, IHXPartitions.HX_PREPROCESSOR);
		
		return reconciler;
	}
	
	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "//", "" };
	}
}