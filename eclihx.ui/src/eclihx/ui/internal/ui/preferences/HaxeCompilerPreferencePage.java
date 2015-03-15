package eclihx.ui.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eclihx.core.CorePreferenceInitializer;
import eclihx.core.EclihxCore;
import eclihx.core.util.OSUtil;
import eclihx.ui.PreferenceConstants;
import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Preference page for setting up the path to the Haxe compiler.
 */
public class HaxeCompilerPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	// TODO 6 validation for the compiler path

	/**
	 * Field for selecting compiler path.
	 */
	private FileFieldEditor compilerPathField;

	/**
	 * Field for configuring is Problem View should be always opened after
	 * launching with errors.
	 */
	private BooleanFieldEditor alwaysOpenProblemsViewField;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {

		final Composite top = new Composite(parent, SWT.LEFT);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(1, false));

		// Create a composite. Without it each new element of the file field
		// editions is pushed to new line.
		Composite compilerPathComposite = new Composite(top, SWT.NONE);
		compilerPathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final String initialCompilerValue = EclihxCore.getDefault()
				.getPluginPreferences().getString(
							CorePreferenceInitializer.HAXE_COMPILER_PATH);
		
		compilerPathField = new FileFieldEditor(
				CorePreferenceInitializer.HAXE_COMPILER_PATH, "Haxe compiler:",
				true, compilerPathComposite);
		compilerPathField.setStringValue(initialCompilerValue);
		compilerPathField.setEmptyStringAllowed(true);
		compilerPathField.setFileExtensions(OSUtil.getCompilerExtensionFilter());
		compilerPathField.setPage(this);
		
		// Create special group for launching properties.
		Group launchGroup = new Group(top, SWT.FILL);
		launchGroup.setText("Launching");
		GridData launchData = new GridData(GridData.FILL_HORIZONTAL);
		launchData.verticalIndent = 10;
		launchGroup.setLayoutData(launchData);
		launchGroup.setLayout(new GridLayout());

		// Create one more composite for to make spans work
		Composite groupComposite = new Composite(launchGroup, SWT.FILL);
				
		alwaysOpenProblemsViewField = new BooleanFieldEditor(
				PreferenceConstants.HAXE_ALWAYS_OPEN_PROBLEM_VIEW_ON_ERRORS,
				"Always open Problem View after error build", groupComposite);
		alwaysOpenProblemsViewField.setPreferenceStore(EclihxUIPlugin
				.getDefault().getPreferenceStore());
		alwaysOpenProblemsViewField.setPage(this);
		alwaysOpenProblemsViewField.load();
		
		
		return top;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		compilerPathField.setStringValue(EclihxCore.getDefault()
				.getPluginPreferences().getDefaultString(
						CorePreferenceInitializer.HAXE_COMPILER_PATH));

		alwaysOpenProblemsViewField.loadDefault();

		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {

		EclihxCore.getDefault().getPluginPreferences().setValue(
				CorePreferenceInitializer.HAXE_COMPILER_PATH,
				compilerPathField.getStringValue());

		alwaysOpenProblemsViewField.store();

		return super.performOk();
	}
}
