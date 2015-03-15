package eclihx.ui.internal.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Preference page for setting up format options.
 */
public class HaxeFormatOptionsPreferencePage  extends PreferencePage 
		implements IWorkbenchPreferencePage {
	
	private Button bracketNewLineCheck;
	private Button insertTabsCheck;
	private Button oneOperatorOnLineCheck;
	private Button indentOnEmptyLinesCheck;
	private Button explicitEmptyPackageCheck;
	private Text indentWidthTextField;
	
	private String EXPLICIT_EMPTY_PACKAGE_STRING = "Generate \"package ;\" string for default package";
	private String BRACKET_NEW_LINE = "Move curly brackets to new lines";
	private String INSERT_TABS = "Use tab character for indentation";	
	private String ONE_OPERATOR_ON_LINE = "Allow only one operator on the line";
	private String INDENT_ON_EMPTY_LINES = "Make indent on empty lines";
	private String INDENT_WIDTH = "Number of spaces in indentation";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(1, false));
			
		// Sets the layout for the top composite's 
		// children to populate.
		top.setLayout(new GridLayout());
		explicitEmptyPackageCheck = new Button(top, SWT.CHECK);
		explicitEmptyPackageCheck.setText(EXPLICIT_EMPTY_PACKAGE_STRING);
		bracketNewLineCheck = new Button(top, SWT.CHECK);
		bracketNewLineCheck.setText(BRACKET_NEW_LINE);
		oneOperatorOnLineCheck = new Button(top, SWT.CHECK);
		oneOperatorOnLineCheck.setText(ONE_OPERATOR_ON_LINE);
		
		indentOnEmptyLinesCheck = new Button(top, SWT.CHECK);
		indentOnEmptyLinesCheck.setText(INDENT_ON_EMPTY_LINES);
		
		insertTabsCheck = new Button(top, SWT.CHECK);
		insertTabsCheck.setText(INSERT_TABS);
				
		Composite indentComposite = new Composite(top, SWT.FILL);	
		indentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FillLayout indentFillLayout = new FillLayout();
		indentFillLayout.spacing = 5;
		indentComposite.setLayout(indentFillLayout);	
		
		Label indentLabel = new Label(indentComposite, SWT.NONE);
		indentLabel.setText(INDENT_WIDTH);
		
		indentWidthTextField = new Text(indentComposite, SWT.BORDER | SWT.FILL);
		indentWidthTextField.setTextLimit(3);
		indentWidthTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateApplyButton();				
			}
		});
		
		initializeValues();
		
		return top;
	}
	
	@Override
	public boolean isValid() {
		if (!super.isValid()) {
			return false;
		}
		
		String indentText = indentWidthTextField.getText();
		try {
			int ident = Integer.parseInt(indentText);
			if (ident <= 0) {
				this.setErrorMessage("Ident size should be above zero");
				return false;
			}
		} catch (NumberFormatException e) {
			this.setErrorMessage("Ident size is not a valid integer number");
			return false;
		}
		
		this.setErrorMessage(null);
		return true;			
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {		
		
		explicitEmptyPackageCheck.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE));
		bracketNewLineCheck.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_BRACKET_NEW_LINE));
		insertTabsCheck.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS));
		oneOperatorOnLineCheck.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_ONE_OPERATOR_ON_LINE));
		indentOnEmptyLinesCheck.setSelection(getPreferenceStore().getDefaultBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_ON_EMPTY_LINES));
		indentWidthTextField.setText(getPreferenceStore().getDefaultString(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH));
		
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {		
		IPreferenceStore store = getPreferenceStore();
		
		store.setValue(PreferenceConstants.HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE, explicitEmptyPackageCheck.getSelection());
		store.setValue(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_BRACKET_NEW_LINE, bracketNewLineCheck.getSelection());
		store.setValue(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS, insertTabsCheck.getSelection());
		store.setValue(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_ONE_OPERATOR_ON_LINE, oneOperatorOnLineCheck.getSelection());
		store.setValue(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_ON_EMPTY_LINES, indentOnEmptyLinesCheck.getSelection());
		store.setValue(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH, indentWidthTextField.getText());
				
		return super.performOk();
	}	
		
	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		
		explicitEmptyPackageCheck.setSelection(store.getBoolean(PreferenceConstants.HX_PACKAGE_PROPERTIES_DEFAULT_PACKAGE));
		bracketNewLineCheck.setSelection(store.getBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_BRACKET_NEW_LINE));
		insertTabsCheck.setSelection(store.getBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INSERT_TABS));
		oneOperatorOnLineCheck.setSelection(store.getBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_ONE_OPERATOR_ON_LINE));
		indentOnEmptyLinesCheck.setSelection(store.getBoolean(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_ON_EMPTY_LINES));
		indentWidthTextField.setText((store.getString(PreferenceConstants.HX_FORMAT_OPTION_PROPERTIES_INDENT_WIDTH)));
	}	
		
	/**
	 * Gets Eclihx UI plug-in preference store
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {		
		return EclihxUIPlugin.getDefault().getPreferenceStore();
	}
}