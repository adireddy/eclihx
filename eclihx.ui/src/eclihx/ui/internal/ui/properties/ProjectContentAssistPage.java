package eclihx.ui.internal.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.model.InvalidBuildFileNameException;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.ui.internal.ui.EclihxUIPlugin;
import eclihx.ui.internal.ui.utils.StandardDialogs;

/**
 * Page for configuring project content assist options
 */
public class ProjectContentAssistPage extends PropertyPage {

	private Text buildFileNameText;
	private Button projectBuildButton;
	private IHaxeProject haxeProject;	

	@Override
	protected Control createContents(Composite parent) {
		
		final Composite top = new Composite(parent, SWT.LEFT);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(2, false));

		Label compilerPathComposite = new Label(top, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		compilerPathComposite.setLayoutData(gridData);
		compilerPathComposite.setText("Select hxml file for content assist proposals construction");

		buildFileNameText = new Text(top, SWT.BORDER);
		buildFileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buildFileNameText.setEditable(false);
		String buildFileName = haxeProject.getContentAssistBuildFileAbsolute();
		buildFileNameText.setText(buildFileName != null ? buildFileName : "");
		
		projectBuildButton = new Button(top, SWT.NONE);
		projectBuildButton.setText("Build file...");
		projectBuildButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				onBuildFileSelected(event);
			}
		});		
		
		return top;
	}	
	
	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		buildFileNameText.setText("");
	}
	
	@Override
	protected void performApply() {
		try {
			haxeProject.setContentAssistBuildFile(buildFileNameText.getText());
		} catch (InvalidBuildFileNameException e) {
			EclihxUIPlugin.getLogHelper().logError("Should never happen because of createBuildFilesDialog");
			Assert.isTrue(false);
		}
	}
	
	@Override
	public IAdaptable getElement() {
		return haxeProject.getBaseResource();
	}
	
	@Override
	public void setElement(IAdaptable element) {
		Object selectedObject = element.getAdapter(IResource.class);
		if (selectedObject instanceof IProject) {
			haxeProject = EclihxCore.getDefault().getHaxeWorkspace().getHaxeProject( 
					(IProject)element.getAdapter(IResource.class));
			
			if (haxeProject == null) {
				EclihxUIPlugin.getLogHelper().logError("Should only be possible to select Haxe projects");
			}
		} else {
			EclihxUIPlugin.getLogHelper().logError("Should only be possible to select on projects");
		}
	}
	
	/**
	 * Handler for the button of project build files selection
	 */
	protected void onBuildFileSelected(SelectionEvent event) {		
		AbstractElementListSelectionDialog dialog =
			StandardDialogs.createBuildFilesDialog(getShell(), haxeProject, buildFileNameText.getText());

		if (dialog.open() == Window.OK) {
			String buildFileName = dialog.getFirstResult().toString();

			if (buildFileName != null) {
				buildFileNameText.setText(buildFileName);
			} else {
				EclihxUIPlugin.getLogHelper().logInfo("The selected file wasn't found.");
			}
		}
	}
}
