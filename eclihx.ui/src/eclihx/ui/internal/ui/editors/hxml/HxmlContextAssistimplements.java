package eclihx.ui.internal.ui.editors.hxml;

import java.util.ArrayList;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

import eclihx.core.haxe.internal.parser.BuildParamParser;

/**
 * Content assist for hxml-files.
 */
public class HxmlContextAssistimplements extends TemplateCompletionProcessor {

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		// TODO 10 Optimize and refactor
		ArrayList<Template> templates = new ArrayList<Template>();
		
		BuildParamParser hxmlParser = new BuildParamParser();		
		Iterable<String> paramsKeys = hxmlParser.getParametersKeys();
		
		for (String key : paramsKeys) {
			templates.add(new Template(key, key + " option", contextTypeId, key, false));
		}
		
		return templates.toArray(new Template[0]);
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		return new TemplateContextType("params", "params");
	}

	@Override
	protected Image getImage(Template template) {
		return null;
	}

}
