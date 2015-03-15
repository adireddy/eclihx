package eclihx.core.haxe.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;

import eclihx.core.EclihxCore;
import eclihx.core.haxe.internal.configuration.CompilationError;
import eclihx.core.haxe.internal.versioning.VersionInfo;
import eclihx.core.util.language.Pair;

/**
 * Parses Haxe compile errors.
 * 
 * Supported versions: 2.0
 */
public final class HaxeOutputErrorsParser implements IHaxeOutputErrorsParser {

	private static final VersionInfo SUPPORTED_VERSIONS_INFO = VersionInfo.above("2.0");
	
	/**
	 * Regular expression for splitting Haxe error to groups.
	 * (file) : (line number) : (characters) : message
	 */
	private static final Pattern LINE_ERROR_PATTERN = Pattern.compile(
			"(.*)\\:(\\d+):([^:]*):(.*)");
	
	/**
	 * Regular expression for reading errors characters.
	 * characters (first chart) - (last chart)
	 */
	private static final Pattern CHARACTERS_ERROR_PATTERN = Pattern.compile(
			"\\s*characters\\s*(\\d+)\\-(\\d+)\\s*");
	
	/**
	 * Regular expression for reading errors lines range.
	 * lines (first line) - (last line)
	 */
	private static final Pattern LINES_RANGE_ERROR_PATTERN = Pattern.compile(
			"\\s*lines\\s*(\\d+)\\-(\\d+)\\s*");
	
	/**
	 * String which is given in successful build.
	 */
	private static final String SUCCESS_BUILD_STRING = "Building complete";
	
	/**
	 * Read the file name for the error file part.
	 * 
	 * @param fileNamePart a part with the file name.
	 * @return a file name
	 */
	private String processFileName(String fileNamePart) {
		return fileNamePart.trim();
	}
	
	/**
	 * Get the line number for the line number part.
	 * 
	 * @param linePart a substring with the line number.
	 * @return a error line number. null if there's some error.
	 */
	private Integer processLineNumber(String linePart) {
		try {
			return Integer.parseInt(linePart);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Read the start and end integer pair from the string and regular expression.
	 * 
	 * @param stringPart a substring with desired integer pair.
	 * @param pattern a pattern for matching integer pair.
	 * @return a pair with the start and end character. Null if there are errors in matching.
	 */
	private Pair<Integer, Integer> processIntPair(String stringPart, Pattern pattern) {
		
		final Matcher matcher = pattern.matcher(stringPart);
		
		if (matcher.matches()) {	
			try {
				int firstInt = Integer.parseInt(matcher.group(1));
				int secondInt = Integer.parseInt(matcher.group(2));
				
				return new Pair<Integer, Integer>(firstInt, secondInt);
			} catch (NumberFormatException numberFormatException) {
				Assert.isTrue(false, "Should be always true because of the matcher expression");
			}
		}
		
		return null;
	}
	
	/**
	 * Gives the message from the message part of the error string 
	 * @param messagePart the part with the message.
	 * @return error message.
	 */
	private String processMessage(String messagePart) {
		return messagePart.trim();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.internal.IHaxeOutputErrorsParser#processErrorLine(java.lang.String)
	 */
	public ICompilerError processErrorLine(String errorLine) {
		
		final Matcher matcher = LINE_ERROR_PATTERN.matcher(errorLine.replaceAll("[\r\n]", ""));
		
		if (matcher.matches()) {			
			String filePath = processFileName(matcher.group(1));
			Integer lineNumber = processLineNumber(matcher.group(2));
			Pair<Integer, Integer> charsRange = processIntPair(matcher.group(3), CHARACTERS_ERROR_PATTERN);
			Pair<Integer, Integer> linesRange = processIntPair(matcher.group(3), LINES_RANGE_ERROR_PATTERN);
			String message = processMessage(matcher.group(4));
			
			if (!(filePath == null || lineNumber == null || message == null)) {
				
				Assert.isTrue(charsRange == null || linesRange == null, "Could they be both not equal to null?");
				
				if (charsRange != null) {
					return CompilationError.getCharRangeError(filePath, lineNumber, charsRange, message);
				}
				
				if (linesRange != null) {
					return CompilationError.getLineRangeError(filePath, lineNumber, linesRange, message);
				}				
			}			
		}
		
		// This is not a Haxe compilation error
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eclihx.core.haxe.internal.IHaxeOutputErrorsParser#parseErrors(java.lang.String)
	 */
	@Override
	public List<ICompilerError> parseErrors(String output, String buildFile) {
		
		ArrayList<ICompilerError> errorsList = new ArrayList<ICompilerError>(); 
		
		// Check build is success
		if (!output.contains(SUCCESS_BUILD_STRING)) {
			
			// Sometimes line delimiter is \r, sometimes \n, sometimes \n\r - replace them all to single \n 
			String outputStr = output.replaceAll("\r", "\n").replaceAll("\n\n", "\n");
			
			// It's expected that each line contains a error.
			for (String line : outputStr.split("\n")) {
				if (!line.isEmpty()) {
					ICompilerError error = processErrorLine(line);
					if (error != null) {
						errorsList.add(error);
					} else {
						// Error processing failed and we add to the build file
						errorsList.add(CompilationError.getLineRangeError(buildFile, 0,
								new Pair<Integer, Integer>(0, 0), line));
					}
				}
			}
			
			// We should find errors, because build wasn't successful.
			Assert.isTrue(!errorsList.isEmpty());
		}
		
		return errorsList;
	}

	@Override
	public VersionInfo getVersionInfo() {
		return SUPPORTED_VERSIONS_INFO;
	}
}
