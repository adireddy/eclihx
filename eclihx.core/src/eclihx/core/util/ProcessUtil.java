package eclihx.core.util;

// Originally based on ProcessUtil class from OCaml Development Tools project.

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eclihx.core.EclihxCore;

/**
 * This class contains utility method for processes.
 */
public class ProcessUtil {
	
	/**
	 * Information about process execution.
	 */
	public static class ProcessExecResult
	{
		private final String errorString;
		private final String outputString;
		private final int returnCode;
		
		/**
		 * @param errorString Error stream string. 
		 * @param outputString Process output stream string.
		 * @param returnCode Execution return code.
		 */
		public ProcessExecResult(String errorString, String outputString, int returnCode) {
			this.errorString = errorString;
			this.outputString = outputString;
			this.returnCode = returnCode;
		}
		
		/**
		 * Error stream string.
		 * @return Error stream string. 
		 */
		public String getErrorsString() {
			return errorString;
		}

		/**
		 * Process output stream string.
		 * @return Process output stream string.
		 */
		public String getOutputString() {
			return outputString;
		}
		
		/**
		 * Execution return code.
		 * @return Execution return code.
		 */
		public int getReturnCode() {
			return returnCode;
		}
	}
	
	/**
	 * Reads input stream in separate thread.
	 */
	private static class StreamReaderThread extends Thread
	{
	    final InputStream readedStream;
	    final StringBuilder streamString;
	    
	    /**
	     * Constructor
	     * @param readedStream stream to read.
	     */
	    StreamReaderThread(InputStream readedStream)
	    {
	        this.readedStream = readedStream;
	        this.streamString = new StringBuilder();
	    }
	    
	    /*
	     * (non-Javadoc)
	     * @see java.lang.Thread#run()
	     */
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(readedStream));

				String line = null;
				while ((line = reader.readLine()) != null) {
					streamString.append(line);
					streamString.append((char)Character.LINE_SEPARATOR);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		/**
		 * Get the string from the stream.
		 * @return The string from the stream.
		 */
		public String getStreamString() {
			return streamString.toString();	
		}
	}

    /** 
     * Class isn't supposed to be instantiated. 
     */
    private ProcessUtil() { }
    
    /**
     * Executes the command line at the given project location, and returns the 
     * correspondent output.
     * 
     * @param commandLine the command line
     * @param workingDirectory the file with execution working directory.
     * @return Information about process execution.
     */
    public static ProcessExecResult executeProcess(String commandLine, File workingDirectory) {
    	
    	EclihxCore.getLogHelper().logInfo(
    			String.format("Eclihx ExecuteProcess. WorkingDirectory: \"%s\". CommandLine: \"%s\".",
    					workingDirectory, commandLine));
    	
    	try {
	    	Process process = Runtime.getRuntime().exec(commandLine, null, workingDirectory);
			
			final StreamReaderThread errorStreamReader = new StreamReaderThread(process.getErrorStream());
			final StreamReaderThread processOutputReader = new StreamReaderThread(process.getInputStream());
	        
	        errorStreamReader.start();
			processOutputReader.start();
	        
	        try {
	        	int returnCode = process.waitFor();
	        	
	        	return new ProcessExecResult(
	        			errorStreamReader.getStreamString(), 
	        			processOutputReader.getStreamString(), 
	        			returnCode);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
    }    
}
