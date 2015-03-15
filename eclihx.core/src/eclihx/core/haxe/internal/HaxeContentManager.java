package eclihx.core.haxe.internal;

import eclihx.core.haxe.model.HaxeSourceFile;
import eclihx.core.haxe.model.core.IHaxePackage;
import eclihx.core.haxe.model.core.IHaxeSourceFile;

/**
 * Haxe file content singleton
 */
public final class HaxeContentManager {
	
	private static HaxeContentManager _instance = null;
	
	/**
	 * This variable has the true value if new Haxe file in default
	 * package should automatically include "package ;" string
	 */
	private boolean isEmptyPackagesEnabled;
	
	/**
	 * Check if empty package string generating is enabled.
	 * 
	 * @return the state of the empty package string generating option.
	 */
	public boolean isEmptyPackagesEnabled() {
		return isEmptyPackagesEnabled;
	}

	/**
	 * Set auto generate empty package string option.
	 * 
	 * @param isEmptyPackagesEnabled the isEmptyPackagesEnabled to set
	 */
	public void setEmptyPackagesEnabled(boolean isEmptyPackagesEnabled) {
		this.isEmptyPackagesEnabled = isEmptyPackagesEnabled;
	}

	private HaxeContentManager() {	}

	/**
	 * Get the instance of the HaxeFileContent.
	 * @return HaxeFileContent instance.
	 */
	public static synchronized HaxeContentManager getInstance() {
		if (_instance == null)
			_instance = new HaxeContentManager();
		return _instance;
	}	
	
	/**
	 * Add content to file except package declaration.
	 * 
	 * @param haxeFile Haxe file for content generation.
	 * @return file content.
	 */
	public String createHaxeContent(IHaxeSourceFile haxeFile) {

		StringBuilder contentBuilder = new StringBuilder();
		
		IHaxePackage haxePackage = haxeFile.getPackage();
		
		if (!haxePackage.isDefault()) {
			contentBuilder.append(String.format("package %s;\n\n", haxePackage.getName()));
		} else {
			if (isEmptyPackagesEnabled) {
				contentBuilder.append("package ;\n\n");
			}
		}
		
		String className = HaxeSourceFile.getClassName(haxeFile.getName());
		String initClassDeclaration = String.format("class %s {\n\n}", className); 
		
		contentBuilder.append(initClassDeclaration);
		
		return contentBuilder.toString();
	}
	
}
