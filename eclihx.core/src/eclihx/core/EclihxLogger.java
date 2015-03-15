package eclihx.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * Eclihx Logger Class.
 */
public final class EclihxLogger implements IPluginLogger {

	/**
	 * Plug-in identification number.
	 */
	private final String pluginID;
	
	/**
	 * Plug-in this logger should work for.
	 */
	private final Plugin plugin;
	
	/**
	 * Constructor.
	 * @param pluginID id of the plug-in
	 * @param plugin Plug-in this logger should work for.
	 */
	public EclihxLogger(Plugin plugin, String pluginID) {
		this.pluginID = pluginID;
		this.plugin = plugin;
	}
	
	/**
	 * For getting the code location of the error we will need to move several
	 * methods up on stack. The value of offset depends of number of utility
	 * methods called for logging.
	 */
	private static final int STACK_OFFSET = 4;
	
	/**
	 * Line of the error.
	 * @return the line of the error.
	 */
    private int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_OFFSET].getLineNumber();
	}

    /**
     * Name of the file with the error.
     * @return the name of the file with the error.
     */
	private String getFileName() {
        return Thread.currentThread().getStackTrace()[STACK_OFFSET].getFileName();
	}

	/**
     * Name of the class where the error occurred.
     * @return the name the class.
     */
	private String getClassName() {
		return Thread.currentThread().getStackTrace()[STACK_OFFSET].getClassName();
	}

	/**
	 * Method name where logging was called.
	 * @return the method name where logging was called.
	 */
	private String getMethodName() {
		return Thread.currentThread().getStackTrace()[STACK_OFFSET].getMethodName();
	}

	/**
	 * Get position of the logging where error or message was logged.
	 * @return position in source code of logging activity.
	 */
    private String getPosition() {
        return getFileName() + " " + getClassName() + "#" + getMethodName() + 
        		":" + getLineNumber();
    }
	
	/**
	 * Log message with info status.
	 * @param message message to log.
	 */
	public void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}

	/**
	 * Log exception with error status.
	 * @param exception exception to log. 
	 */
	public void logError(Throwable exception) {
		logError("Unexpected Exception", exception);
	}

	/**
	 * Log exception with the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 */
	public void logError(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.ERROR, message, exception);
	}
	
	/**
	 * Log error message.
	 * @param message message to log.
	 */
	public void logError(String message) {
		logError(getPosition() + ' ' + message, null);
	}

	/**
	 * Full log wrapper.
	 * 
	 * @param severity severity of the log message.
	 * @param code status of the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 */
	public void log(int severity, int code, String message,
			Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}

	/**
	 * Method generates status for the Eclihx plug-ins.	
	 * @param severity severity of the log message.
	 * @param code status of the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 * @return Generated status.
	 */
	public IStatus createStatus(int severity, int code, String message,
			Throwable exception) {
		return new Status(
				severity, pluginID, code, message, exception);
	}

	/**
	 * Log the status.
	 * @param status status to log.
	 */
	public void log(IStatus status) {
		plugin.getLog().log(status);
	}
}
