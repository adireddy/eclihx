package eclihx.ui.internal.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Main Eclihx project page.
 */
public class HaxeEditorPreferencePage extends FieldEditorPreferencePage 
		implements IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public HaxeEditorPreferencePage() {
		super(GRID);
		
		setDescription("Eclihx Editor Page. You can change Color and Format Options");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// No fields are here.
		// TODO 3 Provide link to official site
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}	
}
