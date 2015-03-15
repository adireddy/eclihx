package eclihx.core;

import org.eclipse.core.runtime.IStatus;

/**
 * Interface for logging plug-in functionality.
 */
public interface IPluginLogger {
	/**
	 * Log message with info status.
	 * @param message message to log.
	 */
	public void logInfo(String message);

	/**
	 * Log exception with error status.
	 * @param exception exception to log. 
	 */
	public void logError(Throwable exception);

	/**
	 * Log exception with the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 */
	public void logError(String message, Throwable exception);
	
	/**
	 * Log error message.
	 * @param message message to log.
	 */
	public void logError(String message);

	/**
	 * Full log wrapper.
	 * 
	 * @param severity severity of the log message.
	 * @param code status of the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 */
	public void log(int severity, int code, String message, 
			Throwable exception);

	/**
	 * Method generates status for the Eclihx plug-ins.	
	 * @param severity severity of the log message.
	 * @param code status of the message.
	 * @param message message to log.
	 * @param exception exception to log.
	 * @return Generated status.
	 */
	public IStatus createStatus(int severity, int code, String message,
			Throwable exception);

	/**
	 * Log the status.
	 * @param status status to log.
	 */
	public void log(IStatus status);
}