package eclihx.core.haxe.internal.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.Assert;

import eclihx.core.haxe.internal.HaxePreferencesManager;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.HaxeConfigurationList;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration.Platform;
import eclihx.core.util.console.parser.Builder;
import eclihx.core.util.console.parser.IDoubleValue;
import eclihx.core.util.console.parser.IParamExistense;
import eclihx.core.util.console.parser.IStringValue;
import eclihx.core.util.console.parser.core.InitializeParseError;
import eclihx.core.util.console.parser.core.Parameter;
import eclihx.core.util.console.parser.core.ParseError;
import eclihx.core.util.console.parser.core.Parser;

/**
 * Class which should parse Haxe builder parameters and store them
 * into the <code>HaxeConfigurationList</code>
 */
public final class BuildParamParser {
	
	private final static class MultiParameterRestrictor {
		
		private int counter = 0;
		private final String errorMessage;
		
		public MultiParameterRestrictor(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		
		public void check() throws ParseError {
			if (++counter > 1) {
				throw new ParseError(
					String.format(errorMessage)
				);
			}
		}
		
		public void reset() {
			counter = 0;
		}
	}
	
	private final MultiParameterRestrictor mainParam = 
		new MultiParameterRestrictor(
			String.format("Multiple: %s.", 
				HaxePreferencesManager.PARAM_PREFIX_STARTUP_CLASS));
	
	private final MultiParameterRestrictor phpFrontParam = 
		new MultiParameterRestrictor(
			String.format("Multiple: %s.", 
				HaxePreferencesManager.PARAM_PREFIX_STARTUP_CLASS));
	
	private final MultiParameterRestrictor phpLibParamRestrictor = 
		new MultiParameterRestrictor(
			String.format("Multiple: %s.",
					HaxePreferencesManager.PARAM_PREFIX_PHP_LIB_FOLDER));
	
	private final MultiParameterRestrictor jsNamespaceParam = 
		new MultiParameterRestrictor(
			String.format("Multiple: %s.", 
				HaxePreferencesManager.PARAM_PREFIX_JS_NAMESPACE));
	
	
	
	private final MultiParameterRestrictor platformParam =
		new MultiParameterRestrictor(
				String.format("Multiple targets"));	
	
	
	
	private final Parser parser;
	
	private HaxeConfiguration currentConfig = null;	
	private HaxeConfigurationList configList = null;
	private String executeFolder = null;	
	
	// True if parsing new configuration should continue filling the previous configuration
	private boolean continueConfig = false;	
	
	private class LibraryParam implements IStringValue {
		public void save(String value) throws ParseError {
			currentConfig.addLibrary(value);
		}
	}
	
	private class CompilationFlagParam implements IStringValue {
		public void save(String value) throws ParseError {
			currentConfig.addCompilationFlag(value);
		}
	}

	private class SwfOutput implements IStringValue {
		public void save(String value) throws ParseError {
			
			platformParam.check();				
			currentConfig.setPlatform(Platform.Flash);
				
			currentConfig.getFlashConfig().setOutputFile(value);
		}
	}
	
	private class Swf9Output implements IStringValue {
		public void save(String value) throws ParseError {
			
			platformParam.check();
			currentConfig.setPlatform(Platform.Flash);
			
			currentConfig.getFlashConfig().setOutputFile(value);
			currentConfig.getFlashConfig().setVersion(9);
		}
	}
	
	private class SwfVersion implements IDoubleValue {
		public void save(double value) throws ParseError {
			currentConfig.getFlashConfig().setVersion(value);
		}
	}
	
	private class MainClass implements IStringValue {
		public void save(String value) throws ParseError {
			mainParam.check();
			currentConfig.setStartupClass(value);
		}
	}
	
	private class HxmlOrMainStringParam implements IStringValue {

		public void save(String value) throws ParseError {
			
			if (value.endsWith("hxml"))
			{
				continueConfig = true;
				parseInternalFile(value);
			}
			else
			{
				mainParam.check();
				currentConfig.setStartupClass(value);
			}
		}		
	}
	
	private class SourceDirectory implements IStringValue {
		public void save(String value) throws ParseError {
			currentConfig.addSourceDirectory(value);	
		}		
	}
	
	private class NekoOutput implements IStringValue {
		@Override
		public void save(String value) throws ParseError {
			platformParam.check();
			currentConfig.setPlatform(Platform.Neko);
			currentConfig.getNekoConfig().setOutputFile(value);
		}
	}
	
	private void initParserParams() {
		Parameter params[] = new Parameter[] {
			
			// -cp <path> : add a directory to find source files
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_SOURCE_DIRECTORY, 
				new SourceDirectory()),
						
			// -js <file> : compile code to JavaScript file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_JAVA_SCRIPT_OUTPUT, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						platformParam.check();
						currentConfig.setPlatform(Platform.JavaScript);
						currentConfig.getJSConfig().setOutputFile(value);
					}
				}),
				
			// -as3 <directory> : generate AS3 code into target directory
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_ACTION_SCRIPT3_DIRECTORY, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						platformParam.check();
						currentConfig.setPlatform(Platform.ActionScript);
						currentConfig.getASConfig().setOutputDirectory(value);						
					}
				}),
			
			// -swf <file> : compile code to Flash SWF file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_SWF_OUTPUT, 
				new SwfOutput()),
				
			// -swf9 <file> : compile code to Flash9 SWF file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_SWF9_OUTPUT, 
				new Swf9Output()),
			
			// -swf-version <version> : change the SWF version (6,7,8,9)
			Builder.createDoubleParam(
				HaxePreferencesManager.PARAM_PREFIX_SWF_VERSION, 
				new SwfVersion()),
			
			// -swf-header <header> : define SWF header (width:height:fps:color)
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_SWF_HEADER, 
				new IStringValue () {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.getFlashConfig().setHeader(value);
					}
				}),
			
			// -swf-lib <file> : add the SWF library to the compiled SWF
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_SWF_LIB,
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.getFlashConfig().addLibrary(value);
					}
				}),
			
			// -neko <file> : compile code to Neko Binary
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_NEKO_OUTPUT, 
				new NekoOutput()),
			
			// -php <directory> : generate PHP code into target directory
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_PHP_DIRECTORY, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						platformParam.check();
						currentConfig.setPlatform(Platform.PHP);
						currentConfig.getPHPConfig().setOutputDirectory(value);
					}
				}),
			
			// -cpp <directory> : generate C++ code into target directory
			Builder.createStringParam(
					HaxePreferencesManager.PARAM_PREFIX_CPP_DIRECTORY, 
					new IStringValue() {
						@Override
						public void save(String value) throws ParseError {
							platformParam.check();
							currentConfig.setPlatform(Platform.CPP);
							currentConfig.getCPPConfig().setOutputDirectory(value);
						}
					}),
			
			// -x <file> : shortcut for compiling and executing a neko file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_NEKO_SHORT_OUTPUT, 
				new NekoOutput()),
			
			// -xml <file> : generate XML types description
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_XML_DESCRIPTION_OUTPUT, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.setXmlDescriptionFile(value);
					}
				}),
			
			// -main <class> : select startup class
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_STARTUP_CLASS, 
				new MainClass()),
			
			// -lib <library[:version]> : use an haxelib library
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_HAXE_LIB, 
				new LibraryParam()),
			
			// -D <var> : define a conditional compilation flag
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_COMPILATION_FLAG, 
				new CompilationFlagParam()),
			
			// -resource <file@name> : add a named resource file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_RESOURCE_FILE, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.addResource(value);
					}
				}),
			
			// -exclude <filename> : don't generate code for classes listed in this file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_EXCLUDE_FILE, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.addExcludeFile(value);
					}
				}),
			
			// -v : turn on verbose mode
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_VERBOSE_MODE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableVerbose();
						}
					}					
				}),
			
			// -debug : add debug informations to the compiled code
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_DEBUG_MODE_FLAG, 
				new IParamExistense() {
					public void save(boolean exist) {
						if (exist) {
							currentConfig.enableDebug();
						}
					}
				}),			
			
			// -prompt : prompt on error
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_PROMT_ERROR_MODE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enablePromptOnErrorMode();
						}
					}
				}),
			
			// -cmd : run the specified command after successful compilation
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_CMD_COMMAND, 
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.addCmdCommand(value);
					}
				}),
			
			// --flash-strict : more type strict flash API
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_FLASH_STRICT_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableflashStrictMode();
						}						
					}
				}),
			
			// --no-traces : don't compile trace calls in the program
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_NO_TRACES_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableNoTracesMode();
						}
					}
				}),
			
			// --flash-use-stage : place objects found on the 
			// stage of the SWF lib
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_FLASH_USE_STAGE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableFlashUseStageMode();
						}
					}
				}),
			
			// --neko-source : keep generated neko source
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_NEKO_SOURCE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.getNekoConfig().
								enableKeepNekoSource();
						}
					}
				}),
			
			//  --gen-hx-classes <file> : generate hx headers from SWF9 file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_GENERATE_HAXE_CLASSES_SWF,
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.setSwfFileForHeaders(value);
					}
				}),
			
			//  --next : separate several Haxe compilations
			// We shouldn't meet this in parsing. This parameter should be 
			// processed on another level of parsing.
			
			//  --display : display code tips
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_CODE_TIPS_FLAG,
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						
						String[] splitResult = value.split("@");
						
						if (splitResult.length != 2) {
							throw new ParseError("Invalid format of --debug option");
						}
						
						try {
							int position = Integer.parseInt(splitResult[1]);
							currentConfig.enableTips(splitResult[0], position);
						} catch (NumberFormatException e) {
							throw new ParseError(e.getMessage());
						}						
					}
				}
			),
			
			//  --no-output : compiles but does not generate any file
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_NO_OUTPUT_FLAG,
				new IParamExistense () {
					public void save(boolean exist) {
						if (exist) {
							currentConfig.setExplicitNoOutput();
						}
					}
				}),
			
			
			//  --times : mesure compilation times
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_TIME_MESURE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableTimeMesureMode();
						}
					}
				}),
			
			//  --no-inline : disable inlining
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_NO_INLINE_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableNoInlineMode();
						}						
					}
				}),
				
			//  --no-opt : disable code optimizations
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_NO_OPTIMIZATION_FLAG, 
				new IParamExistense() {
					@Override
					public void save(boolean exist) throws ParseError {
						if (exist) {
							currentConfig.enableNoOptimizationMode();
						}						
					}
				}),
			
			//--php-front <filename> : select the name for the php front file
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_PHP_FRONT_FILE,
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						phpFrontParam.check();
						currentConfig.getPHPConfig().setFrontFile(value);						
					}
				}),
				
			// --php-lib <filename> : select the name for the php lib folder
			Builder.createStringParam(
					HaxePreferencesManager.PARAM_PREFIX_PHP_LIB_FOLDER,
					new IStringValue() {
						@Override
						public void save(String value) throws ParseError {
							phpLibParamRestrictor.check();
							currentConfig.getPHPConfig().setLibFolderPath(value);
						}
					}),

				
			// --js-namespace <namespace> : create a namespace where root
			Builder.createStringParam(
					HaxePreferencesManager.PARAM_PREFIX_JS_NAMESPACE,
					new IStringValue() {
						@Override
						public void save(String value) throws ParseError {
							jsNamespaceParam.check();
							currentConfig.getJSConfig().setNamespace(value);
						}
					}),
				
			// --remap <package:target> : remap a package to another one
			Builder.createStringParam(
				HaxePreferencesManager.PARAM_PREFIX_REMAP_PACKAGE,
				new IStringValue() {
					@Override
					public void save(String value) throws ParseError {
						currentConfig.addRemapPackage(value);
					}
				}),
				
			// --interp : interpret the program using internal macro system
			Builder.createFlagParam(
					HaxePreferencesManager.PARAM_PREFIX_INTERP, 
					new IParamExistense() {
						@Override
						public void save(boolean exist) throws ParseError {
							currentConfig.enableInterpMode(exist);							
						}
					}),
				
			// --macro  : call the given macro before typing anything else
			Builder.createStringParam(
					HaxePreferencesManager.PARAM_PREFIX_MACRO, 
					new IStringValue() {				
						@Override
						public void save(String value) throws ParseError {
							currentConfig.addMacroCall(value);
						}
					}),
				
			// --dead-code-elimination  : remove unused methods
			Builder.createFlagParam(
					HaxePreferencesManager.PARAM_PREFIX_DEAD_CODE_ELIMINATION, 
					new IParamExistense() {						
						@Override
						public void save(boolean exist) throws ParseError {
							currentConfig.enableDeadCodeElimination(exist);
						}
					}),
			
			//  -help  Display this list of options
			Builder.createFlagParam(
					HaxePreferencesManager.PARAM_PREFIX_HELP1_FLAG,
					new IParamExistense () {
						public void save(boolean exist) {
							if (exist) {
								currentConfig.enableHelp();
							}
						}
					}),
			
			// --help  Display this list of options
			Builder.createFlagParam(
				HaxePreferencesManager.PARAM_PREFIX_HELP2_FLAG,
				new IParamExistense () {
					public void save(boolean exist) {
						if (exist) {
							currentConfig.enableHelp();
						}
					}
				}),
			
			// It could be an hxml file or main class
			Builder.createEmptyStringParam(new HxmlOrMainStringParam())
		};
		
		try {
			parser.initialize(params);
		} catch (InitializeParseError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Default constructor.
	 */
	public BuildParamParser() {
		parser = new Parser();
		initParserParams();
	}
	
	/**
	 * Get a list of accepted hxml options keys.
	 * 
	 * @return A list of accepted configuration options.
	 */
	public Iterable<String> getParametersKeys()
	{	
		return parser.getParametersKeys();
	}
	
	private HaxeConfiguration parseConfiguration(String configStr) throws ParseError {
		
		if (!continueConfig) {
			
			// Reset counters 
			platformParam.reset();
			mainParam.reset();
			phpFrontParam.reset();
			
			currentConfig = new HaxeConfiguration();
		}
		else
		{
			continueConfig = false;
		}
		
		parser.parse(configStr);
		
		return currentConfig;		
	}
	
	private void parse(String str) throws ParseError {
		
		String configStrs[] = str.split("--next");
		
		for (String configStr : configStrs) {
			String trimmedConfig = configStr.trim();
			configList.add(parseConfiguration(trimmedConfig));			
		}
	}
	
	/**
	 * @param executeFolder Folder of the parser execution. Will be used for searching internal hxml files.
	 */
	private void reinitialize(String executeFolder) throws ParseError
	{
		currentConfig = null;
		configList = new HaxeConfigurationList();
		
		File executeFolderPath = new File(executeFolder);
		if (!executeFolderPath.canRead())
		{
			throw new ParseError(String.format("Can't read parser folder: \"%s\"", executeFolder));
		}		
		
		this.executeFolder = executeFolder;
	}
	
	/**
	 * Read file content to string and give it to parser. 
	 * @param fileName
	 * 
	 * @throws ParseError
	 */
	private void parseInternalFile(String filePath) throws ParseError
	{
		File buildFile = new File(filePath);
		if (!buildFile.isAbsolute())
		{
			buildFile = new File(executeFolder, filePath);
		}
		
		if (!buildFile.exists())
		{
			throw new ParseError(String.format("Build file \"%s\" doesn't exist.", buildFile.getPath()));
		}		

		try {
			
			StringBuilder fileContent = new StringBuilder();
			BufferedReader in = new BufferedReader(new FileReader(buildFile));

		    try{
		    	String buffer = null;
				
				while((buffer = in.readLine())!= null) {
					String trimString = buffer.trim();
					
					// We adds only non-comments and not-empty strings
					if (!(trimString.isEmpty() || trimString.startsWith("#"))) {
						
						if (trimString.startsWith("-")) {
							String[] optionValuePair = trimString.split(" ", 2);
							Assert.isTrue(optionValuePair.length != 0);
							
							// Append first parameter.
							fileContent.append(optionValuePair[0]);
							fileContent.append(" ");
							
							if (optionValuePair.length == 2) {
								String value = optionValuePair[1];
								
								// quotes value if necessary
								if (!value.isEmpty()) {
									if (value.contains(" ") && !(value.startsWith("\"") && value.endsWith("\""))) {
										fileContent.append("\"");
										
										// Escape quotes in string
										value = value.replace("\"", "\\\"");									
										
										fileContent.append(value);
										
										fileContent.append("\"");
									} else {
										fileContent.append(value);
									}						
								}
							}							
						} else {
							fileContent.append(trimString);
						}
						
						fileContent.append(" ");
					}
				}
		    }
		    finally{
		    	in.close();
		    }	
		
			parse(fileContent.toString());
			
		} catch (IOException e) {
			throw new ParseError(String.format("Can't read build file: %s.", e.getMessage()));
		}
	}
	
	/**
	 * Parse Haxe configuration from input string. This should be used for parsing console parameters.
	 * @param configStr String to parse.
	 * @param executeFolder Absolute folder path of the parser execution. Will be used for searching internal hxml files.
	 *
	 * @return List of parsed configurations
	 * @throws ParseError An error with the description of the parser problem
	 */
	public HaxeConfigurationList parseString(String configStr, String executeFolder) throws ParseError {
		reinitialize(executeFolder);
		parse(configStr);
		
		return configList;
	}
	
	/**
	 * Parse file with Haxe configuration. 
	 * 
	 * @param filePath Initial hxml file for parsing. 
	 * @param executeFolder Absolute folder path of the parser execution. Will be used for searching internal hxml files.
	 * 
	 * @return List of parsed configurations
	 * @throws ParseError An error with the description of the parser problem
	 */
	public HaxeConfigurationList parseFile(String filePath, String executeFolder) throws ParseError
	{
		reinitialize(executeFolder);
		parseInternalFile(filePath);
		
		return configList;
	}
}
