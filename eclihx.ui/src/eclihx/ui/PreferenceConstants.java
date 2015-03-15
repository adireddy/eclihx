package eclihx.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import eclihx.core.haxe.model.CodeFormatter;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Preference IDs and default values for Eclihx-UI plug-in
 */
@SuppressWarnings("all")
public final class PreferenceConstants {
	
	//========= Haxe Editor Style Settings =========//
	public final static String HX_EDITOR_COMMENT_COLOR = 
		"hx_editor_comment_color";
	public final static String HX_EDITOR_COMMENT_BOLD = 
		"hx_editor_comment_bold";
	public final static String HX_EDITOR_COMMENT_ITALIC = 
		"hx_editor_comment_italic";
	
	public final static String HX_EDITOR_MULTILINE_COMMENT_COLOR = 
		"hx_editor_multiline_comment_color";
	public final static String HX_EDITOR_MULTILINE_COMMENT_BOLD = 
		"hx_editor_multiline_comment_bold";
	public final static String HX_EDITOR_MULTILINE_COMMENT_ITALIC = 
		"hx_editor_multiline_comment_italic";
	
	public final static String HX_EDITOR_STRING_COLOR = 
		"hx_editor_string_color";
	public final static String HX_EDITOR_STRING_BOLD = 
		"hx_editor_string_bold";
	public final static String HX_EDITOR_STRING_ITALIC = 
		"hx_editor_string_italic";
	
	public final static String HX_EDITOR_REGEXPR_COLOR = 
		"hx_editor_regexpr_color";
	public final static String HX_EDITOR_REGEXPR_BOLD = 
		"hx_editor_regexpr_bold";
	public final static String HX_EDITOR_REGEXPR_ITALIC = 
		"hx_editor_regexpr_italic";
	
	public final static String HX_EDITOR_BRACKET_COLOR = 
		"hx_editor_bracket_color";
	public final static String HX_EDITOR_BRACKET_BOLD = 
		"hx_editor_bracket_bold";
	public final static String HX_EDITOR_BRACKET_ITALIC = 
		"hx_editor_bracket_italic";
	
	public final static String HX_EDITOR_BRACE_COLOR = 
		"hx_editor_brace_color";
	public final static String HX_EDITOR_BRACE_BOLD = 
		"hx_editor_brace_bold";
	public final static String HX_EDITOR_BRACE_ITALIC = 
		"hx_editor_brace_italic";
	
	public final static String HX_EDITOR_NUMBER_COLOR = 
		"hx_editor_number_color";
	public final static String HX_EDITOR_NUMBER_BOLD = 
		"hx_editor_number_bold";
	public final static String HX_EDITOR_NUMBER_ITALIC = 
		"hx_editor_number_italic";
	
	public final static String HX_EDITOR_DECLARE_KEYWORDS_COLOR = 
		"hx_editor_declare_keywords_color";
	public final static String HX_EDITOR_DECLARE_KEYWORDS_BOLD = 
		"hx_editor_declare_keywords_bold";
	public final static String HX_EDITOR_DECLARE_KEYWORDS_ITALIC = 
		"hx_editor_declare_keywords_italic";
	
	public final static String HX_EDITOR_KEYWORDS_COLOR = 
		"hx_editor_keywords_color";
	public final static String HX_EDITOR_KEYWORDS_BOLD = 
		"hx_editor_keywords_bold";
	public final static String HX_EDITOR_KEYWORDS_ITALIC = 
		"hx_editor_keywords_italic";
	
	public final static String HX_EDITOR_TYPE_COLOR = "hx_editor_type_color";
	public final static String HX_EDITOR_TYPE_BOLD = "hx_editor_type_bold";
	public final static String HX_EDITOR_TYPE_ITALIC = "hx_editor_type_italic";

	public final static String HX_EDITOR_CONDITIONAL_COMPILATION_COLOR = 
		"hx_editor_conditional_compilation_color";
	public final static String HX_EDITOR_CONDITIONAL_COMPILATION_BOLD = 
		"hx_editor_conditional_compilation_bold";
	public final static String HX_EDITOR_CONDITIONAL_COMPILATION_ITALIC = 
		"hx_editor_conditional_compilation_italic";

	public final static String HX_EDITOR_TEMPLATE_COLOR = 
		"hx_editor_template_color";
	public final static String HX_EDITOR_TEMPLATE_BOLD = 
		"hx_editor_template_bold";
	public final static String HX_EDITOR_TEMPLATE_ITALIC = 
		"hx_editor_template_italic";
	
	public final static String HX_EDITOR_HAXE_DOC_COLOR = 
		"hx_editor_haxe_doc_color";
	public final static String HX_EDITOR_HAXE_DOC_BOLD = 
		"hx_editor_haxe_doc_bold";
	public final static String HX_EDITOR_HAXE_DOC_ITALIC = 
		"hx_editor_haxe_doc_italic";	
	
	public final static String HX_EDITOR_DEFAULT_COLOR = 
		"hx_editor_default_color";
	public final static String HX_EDITOR_DEFAULT_BOLD = 
		"hx_editor_default_bold";
	public final static String HX_EDITOR_DEFAULT_ITALIC = 
		"hx_editor_default_italic";
	
	public final static String HXML_EDITOR_DEFAULT_COLOR = 
		"hxml_editor_default_color";
	public final static String HXML_EDITOR_DEFAULT_BOLD = 
		"hxml_editor_default_bold";
	public final static String HXML_EDITOR_DEFAULT_ITALIC = 
		"hxml_editor_default_italic";
	
	public final static String HXML_EDITOR_OPTION_COLOR = 
		"hxml_editor_option_color";
	public final static String HXML_EDITOR_OPTION_BOLD = 
		"hxml_editor_option_bold";
	public final static String HXML_EDITOR_OPTION_ITALIC = 
		"hxml_editor_option_italic";
	
	public final static String HXML_EDITOR_COMMENT_COLOR = 
		"hxml_editor_comment_color";
	public final static String HXML_EDITOR_COMMENT_BOLD = 
		"hxml_editor_comment_bold";
	public final static String HXML_EDITOR_COMMENT_ITALIC = 
		"hxml_editor_comment_italic";
	
	/**
	 * A named preferences that controls if problem view should always be
	 * activated in the case of Haxe build errors.
	 */
	public final static String HAXE_ALWAYS_OPEN_PROBLEM_VIEW_ON_ERRORS =
			"haxe_always_open_problem_view_on_errors";
	
	//==============================================//
	
	public final static String HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE = 
		"hx_package_properties_default_package";	

	public final static String HX_FORMAT_OPTION_PROPERTIES_BRACKET_NEW_LINE = 
		"hx_format_option_properties_bracket_new_line";
	public final static String HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS = 
		"hx_format_option_properties_insert_tabs";	
	public final static String HX_FORMAT_OPTION_PROPERTIES_ONE_OPERATOR_ON_LINE = 
		"hx_format_option_properties_one_operator_on_line";
	public final static String HX_FORMAT_OPTION_PROPERTIES_INDENT_ON_EMPTY_LINES = 
		"hx_format_option_properties_indent_on_empty_lines";		
	public final static String HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH = 
		"hx_format_option_properties_indent_width";		

	//==============================================//
	
	/**
	 * Initialize given preference store with the default values.
	 * @param store the preference store to be initialized
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		PreferenceConverter.setDefault(
			store, HX_EDITOR_COMMENT_COLOR, new RGB(128, 128, 128));
		store.setDefault(HX_EDITOR_COMMENT_BOLD, false);
		store.setDefault(HX_EDITOR_COMMENT_ITALIC, true);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_MULTILINE_COMMENT_COLOR, new RGB(0, 128, 0));
		store.setDefault(HX_EDITOR_MULTILINE_COMMENT_BOLD, false);
		store.setDefault(HX_EDITOR_MULTILINE_COMMENT_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_STRING_COLOR, new RGB(255, 0, 0));
		store.setDefault(HX_EDITOR_STRING_BOLD, false);
		store.setDefault(HX_EDITOR_STRING_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_BRACKET_COLOR, new RGB(102, 204, 102));
		store.setDefault(HX_EDITOR_BRACKET_BOLD, false);
		store.setDefault(HX_EDITOR_BRACKET_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_BRACE_COLOR, new RGB(102, 204, 102));
		store.setDefault(HX_EDITOR_BRACE_BOLD, false);
		store.setDefault(HX_EDITOR_BRACE_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_NUMBER_COLOR, new RGB(204, 102, 204));
		store.setDefault(HX_EDITOR_NUMBER_BOLD, false);
		store.setDefault(HX_EDITOR_NUMBER_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_REGEXPR_COLOR, new RGB(0, 128, 0));
		store.setDefault(HX_EDITOR_REGEXPR_BOLD, false);
		store.setDefault(HX_EDITOR_REGEXPR_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_DECLARE_KEYWORDS_COLOR, new RGB(0, 0, 0));
		store.setDefault(HX_EDITOR_DECLARE_KEYWORDS_BOLD, true);
		store.setDefault(HX_EDITOR_DECLARE_KEYWORDS_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_KEYWORDS_COLOR, new RGB(0, 0, 102));
		store.setDefault(HX_EDITOR_KEYWORDS_BOLD, false);
		store.setDefault(HX_EDITOR_KEYWORDS_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_TYPE_COLOR, new RGB(0, 0, 102));
		store.setDefault(HX_EDITOR_TYPE_BOLD, false);
		store.setDefault(HX_EDITOR_TYPE_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_CONDITIONAL_COMPILATION_COLOR, 
			new RGB(128, 128, 128));
		store.setDefault(HX_EDITOR_CONDITIONAL_COMPILATION_BOLD, false);
		store.setDefault(HX_EDITOR_CONDITIONAL_COMPILATION_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_TEMPLATE_COLOR, new RGB(0, 0, 255));
		store.setDefault(HX_EDITOR_TEMPLATE_BOLD, false);
		store.setDefault(HX_EDITOR_TEMPLATE_ITALIC, false);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_HAXE_DOC_COLOR, new RGB(128, 128, 128));
		store.setDefault(HX_EDITOR_HAXE_DOC_BOLD, false);
		store.setDefault(HX_EDITOR_HAXE_DOC_ITALIC, true);
		
		PreferenceConverter.setDefault(
			store, HX_EDITOR_DEFAULT_COLOR, new RGB(0, 0, 0));
		store.setDefault(HX_EDITOR_DEFAULT_BOLD, false);
		store.setDefault(HX_EDITOR_DEFAULT_ITALIC, false);
		
		PreferenceConverter.setDefault(
				store, HXML_EDITOR_DEFAULT_COLOR, new RGB(0, 0, 0));
		store.setDefault(HXML_EDITOR_DEFAULT_BOLD, false);
		store.setDefault(HXML_EDITOR_DEFAULT_ITALIC, false);
		
		PreferenceConverter.setDefault(
				store, HXML_EDITOR_COMMENT_COLOR, new RGB(128, 128, 128));
		store.setDefault(HXML_EDITOR_COMMENT_BOLD, false);
		store.setDefault(HXML_EDITOR_COMMENT_ITALIC, true);
		
		PreferenceConverter.setDefault(
				store, HXML_EDITOR_OPTION_COLOR,  new RGB(127, 0, 85));
		store.setDefault(HXML_EDITOR_OPTION_BOLD, true);
		store.setDefault(HXML_EDITOR_OPTION_ITALIC, false);
		
		store.setDefault(HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE, false);
		store.setDefault(HX_FORMAT_OPTION_PROPERTIES_BRACKET_NEW_LINE, false);
		store.setDefault(HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS, false);
		store.setDefault(HX_FORMAT_OPTION_PROPERTIES_ONE_OPERATOR_ON_LINE, true);
		store.setDefault(HX_FORMAT_OPTION_PROPERTIES_INDENT_ON_EMPTY_LINES, false);
		store.setDefault(HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH, 4);
		
		store.setDefault(HAXE_ALWAYS_OPEN_PROBLEM_VIEW_ON_ERRORS, false);
	}
	
	public static IPreferenceStore getPreferenceStore() {
		return EclihxUIPlugin.getDefault().getPreferenceStore();
	}
}
