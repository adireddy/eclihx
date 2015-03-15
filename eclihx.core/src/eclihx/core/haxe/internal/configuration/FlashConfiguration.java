package eclihx.core.haxe.internal.configuration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import eclihx.core.haxe.internal.HaxePreferencesManager;
import eclihx.core.util.OSUtil;

/**
 * Stores and validates flash specific options.
 */
public final class FlashConfiguration extends AbstractConfiguration {
	
	/**
	 * The minimal flash version supporter with haxe compiler.
	 */
	public static final int MINIMAL_FLASH_VERSION = 6;
	
	/**
	 * Path of the output flash file.
	 */
	private String outputFile;
	
	
	/**
	 * Version of the output flash file.
	 */
	private Double version;
	
	
	/**
	 * Flash file header.
	 */
	//TODO 3 Support more properties for this option.
	private String header;
	
	
	/**
	 * Names of files with SWF libraries.
	 */
	private final ArrayList<String> swfLibraries = new ArrayList<String>();

	
	/**
	 * Get the output file path. Can be null if it hasn't been set before.
	 * @return output file path.
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * Set output file. This method does no semantic checks of the path
	 * correctness. Use <code>validate</code> method.
	 * 
	 * @param outputFile the outFile to set. Can't be null. 
	 */
	public void setOutputFile(String outputFile) {
		if (outputFile == null) {
			throw new IllegalArgumentException(
				"Null value isn't allowed for outputFile parameter"); 
		}			
		
		this.outputFile = outputFile;		
	}

	/**
	 * The version of the flash format.
	 * @return the version of the flash format.
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * Sets the version of output flash file. This method does no semantic 
	 * checks of the path correctness - use <code>validate</code> method.  
	 * @param version the version to set
	 */
	public void setVersion(double version) {
		this.version = version;
	}

	/**
	 * Header for the generated swf file.
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Set the header for swf file.
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		// TODO 3 add checks here
		this.header = header;
	}
	
	/**
	 * Method adds swf library file name to configuration.
	 * 
	 * @param libraryFileName The name of the library to add.
	 */
	public void addLibrary(String libraryFileName) {
		swfLibraries.add(libraryFileName);
	}
	
	/**
	 * Get the list of flash library paths 
	 * 
     * @return the list of flash library paths.
	 */
	public Collection<String> getLibraries() {
		return swfLibraries;		
	}

	/**
	 * Prints the configuration.
	 */
	@Override
	public String printConfiguration() throws InvalidConfigurationException {
		
		validateException(); // Possible exceptions here
		
		StringBuilder outputBuilder = new StringBuilder();
		
		// Print output file
		if (version != null && version >= 9) {
			outputBuilder.append(HaxeConfiguration.generateParameter(
				HaxePreferencesManager.PARAM_PREFIX_SWF9_OUTPUT,
				OSUtil.quoteCompoundPath(outputFile)			
			));
		} else {
			// write output file
			outputBuilder.append(HaxeConfiguration.generateParameter(
				HaxePreferencesManager.PARAM_PREFIX_SWF_OUTPUT,
				OSUtil.quoteCompoundPath(outputFile)				
			));
		}

		// Write version. If version == 9 no more parameters are needed
		if (version != null && version != 9) {
			outputBuilder.append(HaxeConfiguration.generateParameter(
				HaxePreferencesManager.PARAM_PREFIX_SWF_VERSION,
				new DecimalFormat("#.##").format(version)
			));					
		}
		
		// It's valid not to include header - so we can omit 
		// it in configuration.		
		if (header != null) {
			// Add header
			outputBuilder.append(HaxeConfiguration.generateParameter(
				HaxePreferencesManager.PARAM_PREFIX_SWF_HEADER,
				header				
			));
		}
		
		// SWF libraries
		for (String libFileName : swfLibraries) {
			outputBuilder.append(HaxeConfiguration.generateParameter(
				HaxePreferencesManager.PARAM_PREFIX_SWF_LIB, 
				OSUtil.quoteCompoundPath(libFileName)
			));		
		}
		
		return outputBuilder.toString();
	}
	
	/**
	 * Performs validation of the configuration and returns description of 
	 * the configuration errors.	
	 * @return Configuration errors.
	 */
	@Override
	protected ArrayList<String> internalValidate() {
		
		ArrayList<String> errorMessages = new ArrayList<String>();
		
		if (outputFile == null) {
			errorMessages.add("No output file for flash target platform.");
		} else {
			// TODO 5 Add file validation
		}
		
		// Absence of the version is a valid situation for Haxe compiler.
		// But if we have changed the default value of the version 
		// we should check it.
		if (version != null) {		
			if (version < MINIMAL_FLASH_VERSION) {
				errorMessages.add(
					"Illegal flash version. haxe supports version above: " +  
					MINIMAL_FLASH_VERSION
				);
			}
		}
		
		// TODO 4 Add validation for header				
		return errorMessages;
	}
}
