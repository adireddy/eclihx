package eclihx.core.haxe.internal;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.CoreException;

import eclihx.core.CorePreferenceInitializer;
import eclihx.core.EclihxCore;
import eclihx.core.haxe.HaxeLauncher;
import eclihx.core.haxe.internal.configuration.HaxeConfiguration;
import eclihx.core.haxe.internal.configuration.HaxeConfigurationList;
import eclihx.core.haxe.internal.configuration.InvalidConfigurationException;
import eclihx.core.haxe.internal.parser.BuildParamParser;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFile;
import eclihx.core.haxe.model.core.IHaxeSourceFolder;
import eclihx.core.util.console.parser.core.ParseError;

/**
 * This class will generate content tips for the particular 
 * positions in the code. 
 */
public class HaxeContextAssistManager {
	
	/**
	 * Exception is thrown if eclihx failed in determining haxe compiler version.
	 */
	@SuppressWarnings("serial")
	public static class TipsEvaluationException extends Exception {
		TipsEvaluationException() { super(); }
		TipsEvaluationException(String message, Throwable cause) { super(message, cause); }
		TipsEvaluationException(String message) { super(message); }
		TipsEvaluationException(Throwable cause) { super(cause); }
	}

	private static final String OPEN_LIST_TAG = "<list>";
	private static final String CLOSE_LIST_TAG = "</list>";
	
	private static final String OPEN_TYPE_TAG = "<type>";
	private static final String CLOSE_TYPE_TAG = "</type>";
	
	private static final String OPEN_ROOT_TAG = "<root>";
	private static final String CLOSE_ROOT_TAG = "</root>";
		
	
	/**
	 * Get the tips for the defined position.
	 * 
	 * @param haxeFile the Haxe file for getting tip. 
	 * @param position offset in file where tip should be got.
	 * @return set of tips. 
	 * @throws TipsEvaluationException if there were errors in tips execution.
	 */
	static public List<ContentInfo> getTips(IHaxeSourceFile haxeFile, int position) throws TipsEvaluationException {

		IHaxeProject project = haxeFile.getHaxeProject();
		if (project == null) {
			return new ArrayList<ContentInfo>();
		}
		
		HaxeConfiguration projectConfiguration = prepareProjectTipsConfiguration(haxeFile, position);
		if (projectConfiguration != null) {
			EclihxCore.getLogHelper().logInfo("Use project tips configuration");
			List<ContentInfo> tips = getTips(projectConfiguration, haxeFile);
			if (!tips.isEmpty()) {
				// Haxe configuration has successfully got proposals.
				return tips;
			}
		}
		
		// There is no project configuration or project configuration hasn't found any tips.
		EclihxCore.getLogHelper().logInfo("Use standard tips configuration");	
		HaxeConfiguration standardConfiguration = getStandardTipsConfiguration(haxeFile, position);
		return getTips(standardConfiguration, haxeFile);
	}
	
	private static HaxeConfiguration getProjectTipsConfiguration(IHaxeProject project) {
		String absoluteContentAssistFilePath = project.getContentAssistBuildFileAbsolute();
		
		if (absoluteContentAssistFilePath != null) {
			
			BuildParamParser buildParamParser = new BuildParamParser();
			
			HaxeConfigurationList configurations;
			try {
				HaxeConfiguration configuration;
				configurations = buildParamParser.parseFile(absoluteContentAssistFilePath, 
						project.getBaseResource().getLocation().toOSString());
				configuration = configurations.getMainConfiguration();
				
				return configuration;
			} catch (Exception e) {
				EclihxCore.getLogHelper().logError(e);
			}
		}
		
		return null;
	}

	private static HaxeConfiguration prepareProjectTipsConfiguration(IHaxeSourceFile haxeFile, int position) {
		
		IHaxeProject project = haxeFile.getHaxeProject();
		HaxeConfiguration haxeConfiguration = getProjectTipsConfiguration(project);
		if (haxeConfiguration != null) {
			haxeConfiguration.setExplicitNoOutput();
			haxeConfiguration.enableTips(haxeFile.getBaseFile().getLocation().toOSString(),	position);
		}
		
		return haxeConfiguration;
	}
	
	private static HaxeConfiguration getStandardTipsConfiguration(IHaxeSourceFile haxeFile, int position) {
		HaxeConfiguration configuration = new HaxeConfiguration();
		IHaxeSourceFolder[] sourceFolders = haxeFile.getHaxeProject().getSourceFolders();
		
		for (IHaxeSourceFolder sourceFolder : sourceFolders) {
			configuration.addSourceDirectory(sourceFolder.getBaseFolder().getLocation().toOSString());
		}
			
		configuration.addClassName(haxeFile.getDefaultClassName());
		configuration.setExplicitNoOutput();
		configuration.enableTips(haxeFile.getBaseFile().getLocation().toOSString(),	position);
		
		return configuration;
	}
	
	static private List<ContentInfo> getTips(HaxeConfiguration configuration, IHaxeSourceFile haxeFile) throws TipsEvaluationException {
		try {
			// TODO 9 need a warning here if preference hasn't been set. 
			final String haxePath = 
				EclihxCore.getDefault().getPluginPreferences().getString(
					CorePreferenceInitializer.HAXE_COMPILER_PATH); 
			
			File workingDirectory = haxeFile.getHaxeProject().getProjectBase().getLocation().toFile();
			HaxeLauncher launcher = new HaxeLauncher();
			launcher.run(configuration, null, haxePath, workingDirectory);
			
			final String errors = launcher.getErrorString();			
			EclihxCore.getLogHelper().logInfo("Haxe Tips Output: " + errors);
			
			{
				// Read type content assist info.
				int startInfoListIndex = errors.indexOf(OPEN_LIST_TAG);
				int endInfoListIndex = errors.indexOf(CLOSE_LIST_TAG);
				
				if (startInfoListIndex != -1 && endInfoListIndex != -1) {
					final String typeTips = errors.substring(startInfoListIndex, endInfoListIndex + CLOSE_LIST_TAG.length());
					return processTypeTips(typeTips);
				}
			}
			
			{
				// Read call information.
				int startFunctionDescription = errors.indexOf(OPEN_TYPE_TAG);
				int endFunctionDescription = errors.indexOf(CLOSE_TYPE_TAG);
				
				if (startFunctionDescription != -1 && endFunctionDescription != -1) {
					final String callTips = errors.substring(startFunctionDescription, 
							endFunctionDescription + CLOSE_TYPE_TAG.length());
					return processCallTips(callTips);
				}
			}
			
		} catch (Exception e) {
			throw new TipsEvaluationException(e);
		}
		
		return new ArrayList<ContentInfo>();
	}
	
	/**
	 * Get haxe classes tips for current package.
	 * @param haxePackage package request is interested in.
	 * @return List with the content information. Won't be null.
	 * @throws TipsEvaluationException if there were errors in tips execution.
	 */
	public static List<ContentInfo> getClassTips(IHaxePackage haxePackage) throws TipsEvaluationException {
		try {			
			
			HaxeConfiguration projectTipsConfiguration = getProjectTipsConfiguration(
					haxePackage.getSourceFolder().getHaxeProject());
			
			if (projectTipsConfiguration != null) {
				EclihxCore.getLogHelper().logInfo("Using project configuration for class tips");
				projectTipsConfiguration.setExplicitNoOutput();
				projectTipsConfiguration.enableClassTips();
				
				List<ContentInfo> classTips = getClassTips(projectTipsConfiguration, haxePackage);
				if (!classTips.isEmpty()) {
					return classTips;
				}
			}
			
			EclihxCore.getLogHelper().logInfo("Using default configuration for class tips");
			HaxeConfiguration haxeConfiguration = new HaxeConfiguration();
			haxeConfiguration.enableClassTips();
			
			return getClassTips(haxeConfiguration, haxePackage);
			
		} catch (Exception e) {
			throw new TipsEvaluationException(e);
		}
	}

	private static List<ContentInfo> getClassTips(HaxeConfiguration haxeConfiguration, 
			IHaxePackage haxePackage) throws CoreException, JAXBException {
		
		final String haxeCompilerPath = EclihxCore.getDefault().getPluginPreferences().getString(
				CorePreferenceInitializer.HAXE_COMPILER_PATH);
		
		IHaxeProject haxeProject = haxePackage.getSourceFolder().getHaxeProject();
		
		HaxeLauncher launcher = new HaxeLauncher();
		launcher.run(haxeConfiguration, null, haxeCompilerPath, new File(haxeProject.getProjectBase().getLocation().toOSString()));
		
		String errors = launcher.getErrorString();		
		EclihxCore.getLogHelper().logInfo("Haxe Class Tips Output: " + errors);
		
		{
			// Read type content assist info.
			int startInfoListIndex = errors.indexOf(OPEN_LIST_TAG);
			int endInfoListIndex = errors.indexOf(CLOSE_LIST_TAG);
			
			if (startInfoListIndex != -1 && endInfoListIndex != -1) {
				final String typeTips = errors.substring(startInfoListIndex, endInfoListIndex + CLOSE_LIST_TAG.length());
				return processTypeTips(typeTips);
			}
		}
		
		return new ArrayList<ContentInfo>();
	}

	private static List<ContentInfo> processCallTips(final String callTips) throws JAXBException {
		final String wrappedTip = OPEN_ROOT_TAG + callTips + CLOSE_ROOT_TAG;
		
		final JAXBContext context = JAXBContext.newInstance(
				FunctionSignature.class);
		
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final StringReader xmlTipsReader = new StringReader(wrappedTip);		
   
		FunctionSignature functionSignature = 
				(FunctionSignature)unmarshaller.unmarshal(xmlTipsReader);
		
		// Move signature information to ContentInfo object.
		final ContentInfo callFunctionInfo = new ContentInfo();
		callFunctionInfo.setType(functionSignature.getFullType());
		final ArrayList<ContentInfo> infos = new ArrayList<ContentInfo>();
		infos.add(callFunctionInfo);
		
		return infos;
	}
	
	private static List<ContentInfo> processTypeTips(final String typeTips) throws JAXBException {
		
		final JAXBContext context = JAXBContext.newInstance(
				ContentInfoContainer.class);
		
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final StringReader xmlTipsReader = new StringReader(typeTips);		
	
		return ((ContentInfoContainer)unmarshaller.
				unmarshal(xmlTipsReader)).contentInfos;
	}		
}
