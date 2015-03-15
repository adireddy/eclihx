package eclihx.core.haxe.os;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import eclihx.core.EclihxCore;

/**
 * Class stores OS-specific haxe file names and can check if Haxe is installed.
 */
public abstract class PathManager {
	
	/**
	 * A convenience flag
	 * True if platform is win32 based, false otherwise.
	 * @see org.eclipse.core.runtime.Platform#getOS()
	 */
	static private final boolean IS_PLATFORM_WIN32 = Platform.OS_WIN32.equals(Platform.getOS());
	
	/**
	 * Haxe compiler executable file name.
	 */
	static public final String HAXE_COMPILER_FILENAME = IS_PLATFORM_WIN32 ? "haxe.exe" : "haxe";
	
	/**
	 * Haxe library manager executable file
	 */
	static public final String HAXE_HAXELIB_FILENAME = IS_PLATFORM_WIN32 ? "haxelib.exe" : "haxelib";
	
	/**
	 * Haxe environment variable.
	 */
	public static final String HAXE_ENVIRONMENT_VARIABLE = "HAXEPATH";
	
	/**
	 * Checks if Haxe installed.
	 * @return true if Haxe installed.
	 */
	public static boolean isInstalled() {
		return getHaxeDirectory() != null;
	}
	
	/**
	 * Haxe installation directory.
	 * @return Tries to find installed Haxe directory. null if Haxe installation hasn't been found.
	 */
	public static String getHaxeDirectory() {
		
		try {
			return System.getenv(HAXE_ENVIRONMENT_VARIABLE);
		} catch (SecurityException e) {
			EclihxCore.getLogHelper().logError(e);
		}
		
		return null;
	}
	
	/**
	 * Get Haxe compiler path.
	 * @return Haxe compiler path. null if Haxe installation hasn't been found.
	 */
	public static String getHaxeCompiler() {
		
		if (IS_PLATFORM_WIN32) {
			String haxeDir = getHaxeDirectory();
			
			if (haxeDir != null) {
				return (new Path(haxeDir)).append(HAXE_COMPILER_FILENAME).toOSString();
			}
		} else {
			File execHaxeFile = new File("/usr/local/bin/haxe");
			if (execHaxeFile.exists() && execHaxeFile.canExecute()) {
				return execHaxeFile.getPath();
			}
			
			execHaxeFile = new File("/usr/bin/haxe");
			if (execHaxeFile.exists() && execHaxeFile.canExecute()) {
				return execHaxeFile.getPath();
			}			
		}		
		
		return null;			
	}
}
