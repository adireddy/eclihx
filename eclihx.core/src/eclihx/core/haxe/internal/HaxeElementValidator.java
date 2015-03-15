package eclihx.core.haxe.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import eclihx.core.EclihxCore;
import eclihx.core.util.FileNameValidator;
import eclihx.core.util.FileNameValidator.FileValidateVerdict;
import eclihx.core.util.FileNameValidator.FileValidationResult;

/**
 * Class validates different internal Haxe entities.
 */
public final class HaxeElementValidator {
	
	/**
	 * Just an underscore char and nothing more.
	 */
	private final static char UNDERSCORE_CHAR = '_';
	
	/**
	 * OK status for all checks.
	 */
	private final static IStatus OK_STATUS = 
			new Status(Status.OK, EclihxCore.PLUGIN_ID, "");
	
	/**
	 * Method simplify creation of the error status.
	 * @param errorMessage the message which should be written to status.
	 */
	private static IStatus createErrorStatus(String errorMessage) {
		return new Status(
				IStatus.ERROR, EclihxCore.PLUGIN_ID, 
				errorMessage);
	}
	
	
	/**
	 * Method validates the Haxe package name.
	 * @param packageName the name to validate.
	 * @return status of the validation. Error status has the assigned reason of
	 *         the error.
	 */
	public static IStatus validatePackageName(String packageName) {
		
		if (packageName == null) {
			return createErrorStatus("A package name must not be null.");
		}
		
		// For the length validations
		int length = packageName.length();
		
		if (length == 0) {
			return createErrorStatus("A package name must not be empty.");
		}
		
		if (packageName.startsWith(".") || packageName.endsWith(".")) {
			return createErrorStatus(
					"A package name cannot start or end with a dot.");
		}
		

		String[] packagesParts = packageName.split("\\.");
		
		for (String packagePart : packagesParts) {
			
			IStatus identifierStatus = validateIdentifier(packagePart);
			
			if (!identifierStatus.isOK()) {
				return createErrorStatus(
						String.format(
								"Error in \"%s\" identifier. %s", 
								packagePart, identifierStatus.getMessage()));
			}
		
			
			if (Character.isUpperCase(packagePart.charAt(0))) {
				return createErrorStatus(
						"Package names must start with a lowercase letter.");
			}
		}
		
		return OK_STATUS;
	}
	
	/**
	 * Method validates the name of Haxe identifier.
	 * 
	 * @param identifierName the Haxe identifier name.
	 * @return status of the validation. Error status has the assigned reason of
	 *         the error.
	 */
	public static IStatus validateIdentifier(String identifierName) {
		
		if (identifierName == null) {
			return createErrorStatus("A identifier name must not be null.");
		}
		
		if (identifierName.isEmpty()) {
			return createErrorStatus("A identifier name must not be empty.");
		}
		
		if (!(Character.isLetter(identifierName.charAt(0)) || 
			  identifierName.charAt(0) == UNDERSCORE_CHAR)) {
			return createErrorStatus(
					"A identifier name must start with a letter.");
		}
		
		for (char ch : identifierName.toCharArray()) {
			if (!(Character.isLetterOrDigit(ch) || ch == UNDERSCORE_CHAR)) {
				return createErrorStatus(
						"A identifier name must contain only letters, " +
						"digits and underscores.");
			}
		}		
		
		return OK_STATUS;
	}
	
	/**
	 * Method validates the name of Haxe build file.
	 * 
	 * @param buildFileName the name of the file.
	 * @return OK status if name is valid and error status with the error 
	 *         message if name is invalid. 
	 */
	public static IStatus validateBuildFileName(String buildFileName) {
		
		final FileValidationResult validateResult = 
			FileNameValidator.validateFileName(
					buildFileName, HaxePreferencesManager.BUILD_FILE_EXTENSION);
		
		String errorMessage = "";
		
		switch (validateResult.getVerdict()) {
			case Ok:
				return OK_STATUS;
			case NullFileName:
				errorMessage = "A build file name must not be null.";
				break;
			case Empty:
				errorMessage = "A build file name must not be empty.";
				break;
			case InvalidExtension:
				errorMessage = String.format(
						"A build file name must have '%s' extension.",
						HaxePreferencesManager.BUILD_FILE_EXTENSION);
				break;
			case InvalidWithMessage:
				errorMessage = validateResult.getMessage();
				break;
			case InvalidUnknown:
				errorMessage = 
					"A name must be a valid file name.";
		}
		
		assert(validateResult.getVerdict() != FileValidateVerdict.Ok);
		
		// If file name is valid
		return createErrorStatus(errorMessage);
	}
	
	/**
	 * Method validates the name of Haxe file.
	 * 
	 * @param haxeFileName the name of the file.
	 * @return OK status if name is valid and error status with the error 
	 *         message if name is invalid. 
	 */
	public static IStatus validateHaxeFileName(String haxeFileName) {
		
		final FileValidationResult validateResult = 
			FileNameValidator.validateFileName(
					haxeFileName, HaxePreferencesManager.HAXE_FILE_EXTENSION);
		
		switch (validateResult.getVerdict()) {
			case Ok:
				{
					final IPath haxeFile = new Path(haxeFileName);
					final String haxeClassName = haxeFile.removeFileExtension().toString();
					
					// Do special Haxe name checks.
					if (!HaxeElementValidator.validateIdentifier(haxeClassName).isOK()) {
						return createErrorStatus("File name should be a valid identifier.");
					}
					
					Assert.isTrue(!haxeClassName.isEmpty()); // Identifier should check it.
					
					if (!Character.isUpperCase(haxeClassName.charAt(0))) {
						return createErrorStatus("Haxe file name should start with upper-case letter.");
					}
				}	
				
				break;
				
			case NullFileName:
				return createErrorStatus("A Haxe file name must not be null.");
			case Empty:
				return createErrorStatus("A Haxe file name must not be empty.");
			case InvalidExtension:
				return createErrorStatus(String.format(
						"A Haxe file name must have '%s' extension.",
						HaxePreferencesManager.HAXE_FILE_EXTENSION));
			case InvalidWithMessage:
				return createErrorStatus(validateResult.getMessage());
			case InvalidUnknown:
				return createErrorStatus("A name must be a valid file name.");
			default:
				Assert.isTrue(false); // Should never be there!
				break;
		}		
		
		return OK_STATUS;
	}
	
	/**
	 * Method validates the name of Haxe output folder.
	 * 
	 * @param outputFolderName the name of the folder.
	 * @return OK status if name is valid and error status with the error 
	 *         message if name is invalid. 
	 */
	public static IStatus validateHaxeOutputFolderName(
			String outputFolderName) {
		
		if (outputFolderName == null) {
			return createErrorStatus("A output folder name must not be null.");
		}
		
		if (outputFolderName.isEmpty()) {
			return createErrorStatus("A output folder name must not be empty.");
		}
		
		
		IStatus validateStatus = ResourcesPlugin.getWorkspace().validateName(
				outputFolderName, IResource.FOLDER);
		
		if (!validateStatus.isOK()) {
			return createErrorStatus(
					"Error in output folder name: " + 
					validateStatus.getMessage());
		}
		
		return OK_STATUS;		
	}
	
	/**
	 * Method validates the name of Haxe source folder.
	 * 
	 * @param sourceFolderName the name of the folder.
	 * @return OK status if name is valid and error status with the error 
	 *         message if name is invalid. 
	 */
	public static IStatus validateHaxeSourceFolderName(
			String sourceFolderName) {
		
		if (sourceFolderName == null) {
			return createErrorStatus("A source folder name must not be null.");
		}
		
		if (sourceFolderName.isEmpty()) {
			return createErrorStatus("A source folder name must not be empty.");
		}
		
		
		IStatus validateStatus = ResourcesPlugin.getWorkspace().validateName(
				sourceFolderName, IResource.FOLDER);
		
		if (!validateStatus.isOK()) {
			return createErrorStatus(
					"Error in source folder name: " + 
					validateStatus.getMessage());
		}
		
		return OK_STATUS;
	}
	
}
