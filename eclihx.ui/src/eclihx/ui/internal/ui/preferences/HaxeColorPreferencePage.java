package eclihx.ui.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import eclihx.core.util.language.Pair;
import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Page with the preferences for editor.
 */
public class HaxeColorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Class for storing controls for managing syntax setting and
	 * providing restricted API for their manipulation
	 */
	private static final class SyntaxEditorsGroup {
		
		private final ColorSelector colorSelector;
		private final Button boldCheck;
		private final Button italicCheck;
		
		/**
		 * Current setting group
		 */
		private SyntaxPreferencesGroup preferencesGroup;
		
		/**
		 * Creates all editors on the Composite and calls <code>load(null)</code>
		 * at the end
		 * @param parent Composite where controls are to be placed
		 */
		public SyntaxEditorsGroup(Composite parent) {

			GridLayout preferencesLayout = new GridLayout(2, false);
			parent.setLayout(preferencesLayout);
			
			Label labelColor = new Label(parent, SWT.NONE);
			labelColor.setText("Color");
			
			colorSelector = new ColorSelector(parent);
			colorSelector.addListener(
				new IPropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (!event.getNewValue().equals(event.getOldValue())) {
							preferencesGroup.changeColorValue((RGB)event.getNewValue());
						}
					}
				}
			);	
			
			GridData checksData = new GridData();
			checksData.horizontalSpan = 2;
			
			boldCheck = new Button(parent, SWT.CHECK);
			boldCheck.setText("Bold");
			boldCheck.setLayoutData(checksData);
			boldCheck.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						preferencesGroup.changeBoldValue(boldCheck.getSelection());
					}
				}
			);
			
			italicCheck = new Button(parent, SWT.CHECK);
			italicCheck.setText("Italic");
			italicCheck.setLayoutData(checksData);
			italicCheck.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						preferencesGroup.changeItalicValue(italicCheck.getSelection());
					}
				}
			);
			
			load(null);
		}
		
		/**
		 * Disable or enable all controls at once
		 * @param state true if controls should be enabled
		 */
		private void setEnabled(boolean state) {
			colorSelector.setEnabled(state);
			boldCheck.setEnabled(state);
			italicCheck.setEnabled(state);
		}
		
		/**
		 * Loads preference group. If group is null this method
		 * makes all controls disabled
		 * @param group Group of syntax preferences
		 * @see SyntaxPreferencesGroup
		 */
		public void load(SyntaxPreferencesGroup group) {
			preferencesGroup = group;
			setEnabled(preferencesGroup != null);
			refresh();
		}
		
		/**
		 * Asks loaded group for current values 
		 */
		public void refresh() {
			if (preferencesGroup != null) {
				colorSelector.setColorValue(preferencesGroup.getColorValue());
				boldCheck.setSelection(preferencesGroup.isBold());
				italicCheck.setSelection(preferencesGroup.isItalic());
			}
		}		
	}
	
	/**
	 * Class for storing values keys from the storing group. This class
	 * implements Lazy Loading pattern and won't ask preference store
	 * for values until user will ask for it.
	 */
	private final class SyntaxPreferencesGroup {
		
		/**
		 * Help class for storing syntax group values
		 */
		private final class SyntaxValues {
			public RGB color;
			public boolean isBold;
			public boolean isItalic;
		}
		
		
		private final String colorNameProperty;
		private final String boldNameProperty;
		private final String italicNameProperty;		
		
		/**
		 * True if user made an attempt to change the value
		 */
		private boolean changed = false;
		
		
		/**
		 * This flag is set to true when user asks to load default values
		 */
		private boolean defaultRequest = false;
		
		
		/**
		 * Reference to the real values for the group 
		 */
		private SyntaxValues values = null;
		
		
		/**
		 * Name which user will be glad to see in the tree
		 */
		private final String name;
		
		public SyntaxPreferencesGroup(String name, String colorName, String boldName, String italicName) {
			this.name = name;
			colorNameProperty = colorName;
			boldNameProperty = boldName;
			italicNameProperty = italicName;
		}
		
		
		/**
		 * Saves values
		 */
		public void save() {
			if (changed || defaultRequest) {
				PreferenceConverter.setValue(getPreferenceStore(), colorNameProperty, getValues().color);
				getPreferenceStore().setValue(boldNameProperty, getValues().isBold);
				getPreferenceStore().setValue(italicNameProperty, getValues().isItalic);
			}
		}
		
		
		/**
		 * Asks to load default values
		 */
		public void resetToDefaults() {
			defaultRequest = true;
			
			// Reset value
			values = null;
			
			// We need saving now, because we are not sure that value hadn't changed
			// The problem appears because we don't store initial loaded values, so
			// we can't check was there any real changes
			changed = true;
		}		
		
		/**
		 * This method gets real values from the preference store
		 * This method provide implementation of the Lazy Load pattern and
		 * should be used for internal access to values
		 * @return values
		 */
		private SyntaxValues getValues() {
			
			if (values == null) {
				
				values = new SyntaxValues();
				
				if (defaultRequest) {					
					values.color = PreferenceConverter.getDefaultColor(getPreferenceStore(), colorNameProperty);
					values.isBold = getPreferenceStore().getDefaultBoolean(boldNameProperty);
					values.isItalic = getPreferenceStore().getDefaultBoolean(italicNameProperty);					
				} else {					
					values.color = PreferenceConverter.getColor(getPreferenceStore(), colorNameProperty);
					values.isBold = getPreferenceStore().getBoolean(boldNameProperty);
					values.isItalic = getPreferenceStore().getBoolean(italicNameProperty);
				}
				
				defaultRequest = false;
			}
			
			return values;
		}
		
		
		/**
		 * Gets color value in RGB
		 * @return RGB representation of the color
		 */
		public RGB getColorValue() {
			return getValues().color;
		}
		
		
		/**
		 * Sets new color value
		 * @param rgb new color value.
		 */
		public void changeColorValue(RGB rgb) {
			changed = true;
			getValues().color = rgb;
		}
		
		
		/**
		 * Returns true if font for this group is bold
		 * @return true if font is bold
		 */
		public boolean isBold() {
			return getValues().isBold;
		}
		
		
		/**
		 * Sets new value of the boldness
		 * @param newIsBold new value of the boldness
		 */
		public void changeBoldValue(boolean newIsBold) {
			changed = true;
			getValues().isBold = newIsBold;
		}
		
		
		/**
		 * Returns true if font for this group is italic
		 * @return true if font is bold
		 */
		public boolean isItalic() {
			return getValues().isItalic;
		}
		
		
		/**
		 * Sets new value for the italic preference of the font
		 * @param newIsItalic new value for the italic preference of the font
		 */
		public void changeItalicValue(boolean newIsItalic) {
			changed = true;			
			getValues().isItalic = newIsItalic;
		}
		
		/**
		 * Access to human readable name of the group
		 * @return name of the group
		 */
		public String getName() {
			return name;
		}
	}
	
	/**
	 * Haxe source viewer options
	 */
	private final ArrayList<SyntaxPreferencesGroup> haxeOptions = 
		new ArrayList<SyntaxPreferencesGroup>();
	
	/**
	 * Haxe documentation options
	 */
	private final ArrayList<SyntaxPreferencesGroup> haxeDocOptions =
		new ArrayList<SyntaxPreferencesGroup>();
	
	/**
	 * hxml editor options
	 */
	private final ArrayList<SyntaxPreferencesGroup> hxmlOptions =
		new ArrayList<SyntaxPreferencesGroup>();
	
	/**
	 * Single store for all groups for unify working with them.
	 * UI group name -> List of preferences
	 */
	private final ArrayList<Pair<String, ArrayList<SyntaxPreferencesGroup>>> editorPartsOptionsLists = 
		new ArrayList<Pair<String, ArrayList<SyntaxPreferencesGroup>>>();
	
	/**
	 * Controls for syntax options
	 */
	private SyntaxEditorsGroup syntaxEditors = null;	
	
	public void init(IWorkbench workbench) {
		
		setTitle("Editors colors preferences");
		
		editorPartsOptionsLists.add(new Pair<String, ArrayList<SyntaxPreferencesGroup>>("Haxe", haxeOptions));
		editorPartsOptionsLists.add(new Pair<String, ArrayList<SyntaxPreferencesGroup>>("Haxe doc", haxeDocOptions));
		editorPartsOptionsLists.add(new Pair<String, ArrayList<SyntaxPreferencesGroup>>("hxml", hxmlOptions));
		
		{
			// Initialize Haxe code preferences
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Line comment",
					PreferenceConstants.HX_EDITOR_COMMENT_COLOR,
					PreferenceConstants.HX_EDITOR_COMMENT_BOLD,
					PreferenceConstants.HX_EDITOR_COMMENT_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Multiline comment",
					PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_COLOR,
					PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_BOLD,
					PreferenceConstants.HX_EDITOR_MULTILINE_COMMENT_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"String",
					PreferenceConstants.HX_EDITOR_STRING_COLOR,
					PreferenceConstants.HX_EDITOR_STRING_BOLD,
					PreferenceConstants.HX_EDITOR_STRING_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Regular expression",
					PreferenceConstants.HX_EDITOR_REGEXPR_COLOR,
					PreferenceConstants.HX_EDITOR_REGEXPR_BOLD,
					PreferenceConstants.HX_EDITOR_REGEXPR_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Brackets",
					PreferenceConstants.HX_EDITOR_BRACKET_COLOR,
					PreferenceConstants.HX_EDITOR_BRACKET_BOLD,
					PreferenceConstants.HX_EDITOR_BRACKET_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Braces",
					PreferenceConstants.HX_EDITOR_BRACE_COLOR,
					PreferenceConstants.HX_EDITOR_BRACE_BOLD,
					PreferenceConstants.HX_EDITOR_BRACE_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Number",
					PreferenceConstants.HX_EDITOR_NUMBER_COLOR,
					PreferenceConstants.HX_EDITOR_NUMBER_BOLD,
					PreferenceConstants.HX_EDITOR_NUMBER_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Declare keyword",
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_COLOR,
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_BOLD,
					PreferenceConstants.HX_EDITOR_DECLARE_KEYWORDS_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Keyword",
					PreferenceConstants.HX_EDITOR_KEYWORDS_COLOR,
					PreferenceConstants.HX_EDITOR_KEYWORDS_BOLD,
					PreferenceConstants.HX_EDITOR_KEYWORDS_ITALIC
				)
			);
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Basic types",
					PreferenceConstants.HX_EDITOR_TYPE_COLOR,
					PreferenceConstants.HX_EDITOR_TYPE_BOLD,
					PreferenceConstants.HX_EDITOR_TYPE_ITALIC
				)
			);
			
			/*
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Template",
					PreferenceConstants.HX_EDITOR_TEMPLATE_COLOR,
					PreferenceConstants.HX_EDITOR_TEMPLATE_BOLD,
					PreferenceConstants.HX_EDITOR_TEMPLATE_ITALIC
				)
			);*/
			
			haxeOptions.add(
				new SyntaxPreferencesGroup(
					"Default",
					PreferenceConstants.HX_EDITOR_DEFAULT_COLOR,
					PreferenceConstants.HX_EDITOR_DEFAULT_BOLD,
					PreferenceConstants.HX_EDITOR_DEFAULT_ITALIC
				)
			);
		}
		
		{
			// Initialize Haxe doc preferences
			haxeDocOptions.add(
				new SyntaxPreferencesGroup(
					"Common text",
					PreferenceConstants.HX_EDITOR_HAXE_DOC_COLOR,
					PreferenceConstants.HX_EDITOR_HAXE_DOC_BOLD,
					PreferenceConstants.HX_EDITOR_HAXE_DOC_ITALIC
				)
			);
		}
		
		{
			hxmlOptions.add(
				new SyntaxPreferencesGroup(
					"Values",
					PreferenceConstants.HXML_EDITOR_DEFAULT_COLOR,
					PreferenceConstants.HXML_EDITOR_DEFAULT_BOLD,
					PreferenceConstants.HXML_EDITOR_DEFAULT_ITALIC
				)
			);
			
			hxmlOptions.add(
				new SyntaxPreferencesGroup(
					"Options",
					PreferenceConstants.HXML_EDITOR_OPTION_COLOR,
					PreferenceConstants.HXML_EDITOR_OPTION_BOLD,
					PreferenceConstants.HXML_EDITOR_OPTION_ITALIC
				)
			);
			
			hxmlOptions.add(
				new SyntaxPreferencesGroup(
					"Comments",
					PreferenceConstants.HXML_EDITOR_COMMENT_COLOR,
					PreferenceConstants.HXML_EDITOR_COMMENT_BOLD,
					PreferenceConstants.HXML_EDITOR_COMMENT_ITALIC
				)
			);
		}
		
		{  
			// Sort groups
			
			// This comparator take in account only names of the groups and it's
			// used to sort number of groups in alphabetic order.
			Comparator<SyntaxPreferencesGroup> nameComparator = 
				new Comparator<SyntaxPreferencesGroup>() {
					public int compare(SyntaxPreferencesGroup o1,
							SyntaxPreferencesGroup o2) {
						return o1.getName().compareTo(o2.getName());
					}
				};
			
			for (Pair<String, ArrayList<SyntaxPreferencesGroup>> editorPartDescription : 
				editorPartsOptionsLists)
			{
				Collections.sort(editorPartDescription.getSecond(), nameComparator);
			}
		}
	}
	
	
	/**
	 * Creates tree for options
	 * @param parent composite where tree must be placed
	 */
	private void createSyntaxOptionsTree(final Composite parent) {

		final FillLayout treeLayout = new FillLayout();
		parent.setLayout(treeLayout);
		
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.SINGLE);
				
		final LinkedList<TreeItem> rootTreeItems = new LinkedList<TreeItem>();
		
		for (Pair<String, ArrayList<SyntaxPreferencesGroup>> editorPartList : editorPartsOptionsLists) {
			// Create an element and add to root elements
			final TreeItem rootItem = new TreeItem(tree, SWT.NONE);
			rootItem.setText(editorPartList.getFirst());
			rootItem.setData(null);
			rootTreeItems.add(rootItem);
			
			for (SyntaxPreferencesGroup option : editorPartList.getSecond()) {
				TreeItem child = new TreeItem(rootItem, SWT.NONE);
				child.setText(option.getName());
				child.setData(option);
			}
		}
			
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] selectedItems = tree.getSelection();
				
				if (selectedItems.length > 0) {
					TreeItem item = selectedItems[0];
					if (item.getData() instanceof SyntaxPreferencesGroup) {
						syntaxEditors.load((SyntaxPreferencesGroup)item.getData());
					} else {
						syntaxEditors.load(null);
					}						
				}				
			}
		});
	}
	
	@Override
	protected Control createContents(final Composite parent) {

		Composite mainComposite= new Composite(parent, SWT.NONE);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout mainLayout= new GridLayout();
		mainLayout.numColumns = 2;
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComposite.setLayout(mainLayout);

		
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.horizontalSpan = 2;
		
		Link link = new Link(mainComposite, SWT.NONE);
		link.setText("Default colors and font can be configured on the <a href=\"org.eclipse.ui.preferencePages.GeneralTextEditor\">Text Editors</a> and on the <a href=\"org.eclipse.ui.preferencePages.ColorsAndFonts\">Colors and Fonts</a> preference page.");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null, null);
			}
		});		
		link.setLayoutData(gridData);
		
		final GridData titlesData = new GridData(GridData.FILL_HORIZONTAL);
		titlesData.horizontalSpan = 1;
		titlesData.verticalIndent = 20;
		
		final Label treeTitle = new Label(mainComposite, SWT.NONE);
		treeTitle.setText("Editor element");
		treeTitle.setLayoutData(titlesData);
		
		final Label preferenceTitle = new Label(mainComposite, SWT.NONE);
		preferenceTitle.setText("Preferences");
		preferenceTitle.setLayoutData(titlesData);		

		Composite treeComposite = new Composite(mainComposite, SWT.NONE);
		GridData treeData = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeData.widthHint = 100;
		treeComposite.setLayoutData(treeData);
		
		createSyntaxOptionsTree(treeComposite);		
		
		Composite preferencesComposite = new Composite(mainComposite, SWT.NONE | SWT.BORDER);
		GridData preferenceData = new GridData(GridData.FILL_BOTH);
		preferencesComposite.setLayoutData(preferenceData);

		syntaxEditors = new SyntaxEditorsGroup(preferencesComposite);
		
		return parent;
	}
	
	/**
	 * Reset all syntax preferences to defaults values
	 */
	@Override
	protected void performDefaults() {
		
		for (Pair<String, ArrayList<SyntaxPreferencesGroup>> editorPartList : editorPartsOptionsLists)
		{
			for (SyntaxPreferencesGroup option : editorPartList.getSecond()) {
				option.resetToDefaults();
				syntaxEditors.refresh();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		
		for (Pair<String, ArrayList<SyntaxPreferencesGroup>> editorPartList : editorPartsOptionsLists)
		{
			for (SyntaxPreferencesGroup option : editorPartList.getSecond()) {
				option.save();
			}
		}
		
		return super.performOk();
	}

	/**
	 * Gets Eclihx UI plug-in preference store
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		return EclihxUIPlugin.getDefault().getPreferenceStore();
	}
}
