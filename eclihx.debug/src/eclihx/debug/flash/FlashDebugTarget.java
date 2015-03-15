package eclihx.debug.flash;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import flash.tools.debugger.Frame;
import flash.tools.debugger.Location;
import flash.tools.debugger.NoResponseException;
import flash.tools.debugger.NotConnectedException;
import flash.tools.debugger.NotSuspendedException;
import flash.tools.debugger.Session;
import flash.tools.debugger.SuspendReason;
import flash.tools.debugger.VersionException;
import flash.tools.debugger.events.BreakEvent;
import flash.tools.debugger.events.DebugEvent;
import flash.tools.debugger.events.FaultEvent;
import flash.tools.debugger.events.SwfLoadedEvent;
import flash.tools.debugger.events.SwfUnloadedEvent;
import flash.tools.debugger.events.TraceEvent;

public class FlashDebugTarget extends FlashDebugElement implements IDebugTarget {
	
	/**
	 * Listens to events from the Flash VM and fires corresponding 
	 * debug events.
	 */
	class EventDispatchJob extends Job {
		
		public EventDispatchJob() {
			super("Haxe flash event dispatch");
			
			setSystem(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			
			boolean requestResume = false;
			//boolean requestHalt = false;

			while(fSession != null && fSession.getEventCount() > 0)
			{
				DebugEvent event = fSession.nextEvent();

				if (event instanceof TraceEvent)
				{
					//dumpTraceLine(e.information);
				}
				else if (event instanceof SwfLoadedEvent)
				{
					handleSwfLoadedEvent((SwfLoadedEvent)event);
				}
				else if (event instanceof SwfUnloadedEvent)
				{
					handleSwfUnloadedEvent((SwfUnloadedEvent)event);
				}
				else if (event instanceof BreakEvent)
				{
					handleBreakEvent((BreakEvent)event);
				}
				else if (event instanceof FaultEvent)
				{
				}
				else
				{
					//Map<String, Object> args = new HashMap<String, Object>();
					//args.put("type", e); //$NON-NLS-1$
					//args.put("info", e.information); //$NON-NLS-1$
					//err(getLocalizationManager().getLocalizedTextString("unknownEvent", args)); //$NON-NLS-1$
				}
			}
			
			// only if we have processed a fault which requested a resume and no other fault asked for a break
			// and we are suspended and it was due to us that the stop occurred!
			//if (requestResume && !requestHalt && m_session.isSuspended() && m_session.suspendReason() == SuspendReason.Fault)
			//	m_requestResume = true;

			
			return Status.OK_STATUS;
		}

		private void breakpointHit(int lineNumber) {

			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(FlashConstants.ID_FLASH_DEBUG_MODEL);

			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (supportsBreakpoint(breakpoint)) {
					if (breakpoint instanceof ILineBreakpoint) {
						ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
						
						try {
							if (lineBreakpoint.getLineNumber() == lineNumber) {
								//fThread.setBreakpoints(new IBreakpoint[] { breakpoint });
								break;
							}
						} catch (CoreException e) {
						}
					}
				}
			}
			
			suspended(SuspendReason.Breakpoint);
			
		}

		private void handleSwfUnloadedEvent(SwfUnloadedEvent unloadEvent) {
			return;			
		}

		
		private void handleBreakEvent(BreakEvent breakEvent) {
			try {
				switch (fSession.suspendReason()) {
					case SuspendReason.Breakpoint:
						int line = breakEvent.line;
						breakpointHit(line);
						break;
				}				
			} catch (NotConnectedException exception) {
				exception.printStackTrace();
			}
		}

		private void handleSwfLoadedEvent(SwfLoadedEvent loadedEvent) {
			try {
				suspended(fSession.suspendReason());
			} catch (NotConnectedException excetption) {
				excetption.printStackTrace();
			}
		}
		
		
	}
	
	private ILaunch fLaunch;
	private IProcess fProcess;
	private String fUrl;
	private Session fSession;
	private FlashThread fThread;
	private IDebugTarget fTarget;
	private IThread[] fThreads;
	private EventDispatchJob fEventDispatch;
	
	private boolean fSuspended = true;
	private boolean fResumed = true;
	
	private DebugException generateError(String message, Throwable exception) {
		return new DebugException(new Status(IStatus.OK, "debug", message, exception));		
	}
	
	/**
	 * Notification the target has resumed for the given reason
	 * 
	 * @param detail reason for the resume
	 */
	private void resumed(int detail) {
		fSuspended = false;
		fThread.fireResumeEvent(detail);
	}
	
	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail reason for the suspend
	 */
	private void suspended(int detail) {
		fSuspended = true;
		fThread.fireSuspendEvent(detail);
	}	

	public FlashDebugTarget(String url, ILaunch launch, Session session, IProcess process) throws CoreException, VersionException {
		super(null);
		
		fTarget = this;
		fThread = new FlashThread(this);
		fThreads = new IThread[] { fThread };
		fUrl = url;
		fLaunch = launch;
		fProcess = process;
		fSession = session;
		
		if (fSession.bind()) {
			fEventDispatch = new EventDispatchJob();
			fEventDispatch.schedule();

			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);

			breakpointAdded(null);
			try {
				setDeferredBreakpoints();
				resume();
			} catch (DebugException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds to Flash VM all breakpoints which we have already in the code
	 */
	private void setDeferredBreakpoints() {
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		for (IBreakpoint breakpoint : manager.getBreakpoints()) {
			breakpointAdded(breakpoint);
		}
	}
	
	public String getName() throws DebugException {
		// TODO 5 make it beautiful
		return ("Haxe debug: " + fUrl);
	}

	
	public IProcess getProcess() {
		return fProcess;
	}

	
	public IThread[] getThreads() throws DebugException {
		return fThreads;
	}

	
	public boolean hasThreads() throws DebugException {
		return (!isTerminated());
	}

	
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return (breakpoint instanceof FlashBreakpoint);
	}

	
	public IDebugTarget getDebugTarget() {
		return this;
	}

	
	public ILaunch getLaunch() {
		return fLaunch;
	}

	
	public boolean canTerminate() {
		return true;
	}

	
	public synchronized boolean isTerminated() {
		return (!fSession.isConnected());
	}

	
	public void terminate() throws DebugException {
		fSession.terminate();
		
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
	}

	
	public boolean canResume() {
		return (!isTerminated() && isSuspended());
	}

	
	public boolean canSuspend() {
		// If session is started and isn't suspended yet
		return (!(isTerminated() || isSuspended()));
	}

	
	public boolean isSuspended() {
		try {
			return fSession.isSuspended();
		} catch (NotConnectedException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public void resume() throws DebugException {
		try {
			fSession.stepContinue();
		} catch (NotSuspendedException e) {
			e.printStackTrace();
			throw generateError("Can't resume", e);
		} catch (NoResponseException e) {
			e.printStackTrace();
			throw generateError("Can't resume", e);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw generateError("Can't resume", e);
		}
		
	}

	
	public void suspend() throws DebugException {
		try {
			fSession.isSuspended();
		} catch (NotConnectedException e) {
			e.printStackTrace();
			throw generateError("bad suspend", e);
		}
	}

	
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				FlashBreakpoint flashBreak = (FlashBreakpoint)breakpoint; // see supportsBreakpoint method
								
				int line = flashBreak.getLineNumber();
				
				Location location = fSession.setBreakpoint(1, line);
				
				// Update info about breakpoint enable state
				flashBreak.setEnabled(location != null);
				
				//TODO 8 add notification if we disabled the breakpoint
				
			} catch (NoResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO 6 implement this
		
	}

	
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO 8 implement this		
	}

	
	public boolean canDisconnect() {
		return false; // TODO 5 enable disconnecting
		//return (!isDisconnected());
	}

	
	public void disconnect() throws DebugException {
		fSession.unbind();
	}

	
	public synchronized boolean isDisconnected() {
		return (!fSession.isConnected());
	}

	
	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	
	public boolean supportsStorageRetrieval() {
		
		return true;
	}
	
	/**
	 * Returns the current stack frames in the target.
	 * 
	 * @return the current stack frames in the target
	 * @throws DebugException
	 *             if unable to perform the request
	 */
	protected synchronized IStackFrame[] getStackFrames() throws DebugException {

		try {
			Frame frames[];
			frames = fSession.getFrames();

			ArrayList<IStackFrame> stackFrames = new ArrayList<IStackFrame>();
			for (Frame flashFrame : frames) {
				stackFrames.add(new FlashStackFrame(fThread, flashFrame));
			}

			FlashStackFrame stackFramesResult[] = new FlashStackFrame[stackFrames
					.size()];
			return stackFrames.toArray(stackFramesResult);

		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
}
