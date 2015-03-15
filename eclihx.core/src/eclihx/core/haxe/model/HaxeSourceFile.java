package eclihx.core.haxe.model;

import java.security.InvalidParameterException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;

import eclihx.core.haxe.internal.HaxeElementValidator;
import eclihx.core.haxe.internal.HaxePreferencesManager;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeProject;
import eclihx.core.haxe.model.core.IHaxeSourceFile;

/**
 * Class add additional functions to the IFile resource.
 */
public class HaxeSourceFile extends HaxeElement implements IHaxeSourceFile {

	/**
	 * Resource object for this Haxe file.
	 */
	private final IFile fFile;

	/**
	 * Package of this file.
	 */
	private IHaxePackage fPackage;

	/**
	 * Return default class name for the file with the given name.
	 * 
	 * @param fileName the name of the file.
	 * 
	 * @return String with the name.
	 * @throws RuntimeException if file has invalid extension.
	 */
	public static String getClassName(String fileName) {

		Assert.isLegal(fileName.endsWith(HaxePreferencesManager.HAXE_FILE_EXTENSION));
		
		int lastNameIndex = fileName.length()
				- HaxePreferencesManager.HAXE_FILE_EXTENSION.length() - 1;

		String classSubstring = fileName.substring(0, lastNameIndex);

		if (classSubstring.isEmpty()) {
			throw new RuntimeException("Class name can't be empty");
		}

		// Upper case first letter
		// return Character.toUpperCase(classSubstring.charAt(0))
		//		+ classSubstring.substring(1, classSubstring.length());
		
		return classSubstring;
	}

	/**
	 * Construct the Haxe source file object on the base of the IFile resource.
	 * Resource should have a valid Haxe file name.
	 * 
	 * @param file the original resource file.
	 * @param haxePackage Haxe package where this file is situated.
	 */
	public HaxeSourceFile(IFile file, IHaxePackage haxePackage) {
		super(haxePackage);

		if (!HaxeElementValidator.validateHaxeFileName(file.getName()).isOK()) {
			throw new InvalidParameterException("File has invlid name.");
		}

		if (!HaxeProject.isHaxeProject(file.getProject())) {
			throw new InvalidParameterException(
					"Given file should be in the Haxe project");
		}

		if (!file.getParent().equals(haxePackage.getBaseFolder())) {
			throw new InvalidParameterException(
					"Given file doesn't lay in the given package.");
		}

		this.fPackage = haxePackage;
		this.fFile = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getName()
	 */
	@Override
	public String getName() {
		return fFile.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFile#getBase()
	 */
	@Override
	public IFile getBaseFile() {
		return fFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getBaseResource()
	 */
	@Override
	public IResource getBaseResource() {
		return getBaseFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFile#getPackage()
	 */
	@Override
	public IHaxePackage getPackage() {
		return fPackage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFile#getHaxeProject()
	 */
	@Override
	public IHaxeProject getHaxeProject() {
		return fPackage.getSourceFolder().getHaxeProject();
	}

	/**
	 * Get the default Haxe class name for this file but without package prefix.
	 * 
	 * @return the string with the class name.
	 */
	private String getClassName() {
		return getClassName(getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeSourceFile#getDefaultClassName()
	 */
	@Override
	public String getDefaultClassName() {
		String packagePrefix = ""; // Prefix for default package.

		if (!fPackage.isDefault()) {
			packagePrefix = fPackage.getName() + ".";
		}

		return packagePrefix + getClassName();
	}

	/**
	 * Move haxe file.
	 * @param haxePackage move to other Haxe package.
	 */
	public void move(HaxePackage haxePackage) {
		// FIXME 10: tie-in refactorings should be done
		fPackage = haxePackage;
	}
	
}
