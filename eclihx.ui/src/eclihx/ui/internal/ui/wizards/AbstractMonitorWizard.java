package eclihx.ui.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;

import eclihx.ui.internal.ui.EclihxUIPlugin;

/**
 * Extension for the standard {@link Wizard} class with implementation of the
 * progress monitor based operations.
 * 
 * Instead of using <code>performFinish()</code> and
 * <code>performCancel()</code> methods subclasses should override
 * <code>doCancel()</code> and <code>doFinish()</code>.
 */
public abstract class AbstractMonitorWizard extends Wizard {

	/**
	 * Default constructor which enables the using of the progress monitor.
	 */
	public AbstractMonitorWizard() {
		setNeedsProgressMonitor(true);
	}

	/**
	 * Run doFinish with progress.
	 * 
	 * @return <code>true</code> to indicate the cancel request
     * was accepted, and <code>false</code> to indicate
     * that the cancel request was refused.	     
	 */
	@Override
	public final boolean performCancel() {
		
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
				
				new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							doCancel(monitor);
						} catch (Exception e) {

						} finally {
							monitor.done();
						}
					}
				});
		
		try {
			getContainer().run(false, false, op);
		} catch (InvocationTargetException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/**
	 * Run doFinish with progress.
	 * 
	 * @return <code>true</code> to indicate the finish request
     * was accepted, and <code>false</code> to indicate
     * that the finish request was refused.
	 */
	@Override
	public final boolean performFinish() {

		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							doFinish(monitor);
						} catch (Exception e) {

						} finally {
							monitor.done();
						}
						return;
					}

				});
		
		try {
			getContainer().run(false, false, op);
		} catch (InvocationTargetException e) {

			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;

	}

	/**
	 * Override this method to specify cancel operations for the wizard.
	 */
	protected abstract void doCancel(IProgressMonitor monitor);

	/**
	 * Override this method to specify finish operations for the wizard.
	 */
	protected abstract void doFinish(IProgressMonitor monitor);

	/**
	 * Show asynchronous error dialog with the message.
	 * 
	 * @param message the message to show
	 */
	protected void showErrorBox(final String message) {
		
		EclihxUIPlugin.getLogHelper().logError(message);
		
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(getShell(), "Eclihx", message);
			}
		});
	}
	
	/**
	 * Opens file. Use this method to show user just created resource.
	 * 
	 * @param file the resource to open.
	 * 
	 * TODO 4 Think for a better place for this method.
	 */
	protected void openFile(final IFile file) {
		final IWorkbenchPage activePage = 
				EclihxUIPlugin.getDefault().getWorkbench().
						getActiveWorkbenchWindow().getActivePage();
		
		if (file == null) {
			return;
		}
		
		if (activePage != null) {
			final Display display= getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(activePage, file, true);
						} catch (PartInitException e) {
							EclihxUIPlugin.getLogHelper().logError(e);
						}
					}
				});
			}
		}
	}
	
}
