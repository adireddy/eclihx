package eclihx.core.haxe.internal;

import java.util.List;

import eclihx.core.haxe.internal.versioning.IVersionated;

/**
 * Interface for the parser of the Haxe compiler output which is aimed to
 * getting compile errors.
 */
public interface IHaxeOutputErrorsParser extends IVersionated {
	
	/**
	 * Method processes a line with the compile error.
	 * 
	 * Example of the line with the error:
	 * SmallTest/src/Test.hx:3: characters 6-7 : Missing ;
	 * 
	 * @param errorLine a line with the error.
	 * @return new compile error object or null if parsing wasn't success.
	 */
	ICompilerError processErrorLine(String errorLine);
	
	/**
	 * Gives the list of compile errors from the compiler output.
	 * 
	 * @param output Haxe compiler output.
	 * @param buildFile a build file path. 
	 * @return A list of errors.
	 */	
	List<ICompilerError> parseErrors(String output, String buildFile);
}
