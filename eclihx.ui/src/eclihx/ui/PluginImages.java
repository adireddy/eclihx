package eclihx.ui;

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.osgi.framework.Bundle;

import eclihx.ui.internal.ui.EclihxUIPlugin;

// TODO 9: Refactor code

/**
 * On the base of JDT sources.
 * 
 * Bundle of most images used by the Java plug-in.
 * 
 * On the base of jdt.ui
 */
@SuppressWarnings("all")
public class PluginImages {

	private static final IPath ICONS_PATH = new Path("$nl$/icons"); //$NON-NLS-1$

	private static final String NAME_PREFIX = "eclihx.ui."; //$NON-NLS-1$
	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	// The plug-in registry
	private static ImageRegistry fgImageRegistry = null;
	private static HashMap<String, ImageDescriptor> fgAvoidSWTErrorMap = null;
	
	private static final String ASSIST_PREFIX = "assist";
	private static final String WIZARDS_PREFIX = "wizards";
	private static final String UNDEFINED = "";

	/*
	 * Keys for images available from the Eclihx-UI plug-in image register.
	 */
	
	/**
	 * Public function image key.
	 */
	public static final String IMG_MISC_PUBLIC = NAME_PREFIX + "methpub_obj.gif";
	
	/**
	 * Protected function image key.
	 */
	public static final String IMG_MISC_PROTECTED = NAME_PREFIX + "methpro_obj.gif";
	
	/**
	 * Private function image key.
	 */
	public static final String IMG_MISC_PRIVATE = NAME_PREFIX + "methpri_obj.gif";
	
	/**
	 * Default function image key.
	 */
	public static final String IMG_MISC_DEFAULT = NAME_PREFIX + "methdef_obj.gif";
	
	/**
	 * Public field image key.
	 */
	public static final String IMG_FIELD_PUBLIC = NAME_PREFIX + "field_public_obj.gif";
	
	/**
	 * Protected field image key.
	 */
	public static final String IMG_FIELD_PROTECTED = NAME_PREFIX + "field_protected_obj.gif";
	
	/**
	 * Private field image key.
	 */
	public static final String IMG_FIELD_PRIVATE = NAME_PREFIX + "field_private_obj.gif";
	
	/**
	 * Default field image key.
	 */
	public static final String IMG_FIELD_DEFAULT = NAME_PREFIX + "field_default_obj.gif";
	
	/**
	 * Public class image key.
	 */
	public static final String IMG_CLASS_PUBLIC = NAME_PREFIX + "innerclass_public_obj.gif";
	
	/**
	 * Project image key.
	 */
	public static final String IMG_PROJECT = NAME_PREFIX + "h_project_s.gif";
	
	/**
	 * Source folder image key.
	 */
	public static final String IMG_SOURCE_FOLDER = NAME_PREFIX + "sourcefolder_s.gif";
	
	/**
	 * Build file image key.
	 */
	public static final String IMG_BUILD_FILE = NAME_PREFIX + "build_file_s.png";
	
	/**
	 * Package image key.
	 */
	public static final String IMG_PACKAGE = NAME_PREFIX + "h_package_s.gif";
	
	/*
	 * Set of predefined Image Descriptors.
	 */
	public static final ImageDescriptor DESC_MISC_PUBLIC = 
			createManagedFromKey(ASSIST_PREFIX, IMG_MISC_PUBLIC);
	public static final ImageDescriptor DESC_MISC_PROTECTED = 
			createManagedFromKey(ASSIST_PREFIX, IMG_MISC_PROTECTED);
	public static final ImageDescriptor DESC_MISC_PRIVATE = 
			createManagedFromKey(ASSIST_PREFIX, IMG_MISC_PRIVATE);
	public static final ImageDescriptor DESC_MISC_DEFAULT = 
			createManagedFromKey(ASSIST_PREFIX, IMG_MISC_DEFAULT);
	
	public static final ImageDescriptor DESC_FIELD_PUBLIC = 
			createManagedFromKey(ASSIST_PREFIX, IMG_FIELD_PUBLIC);
	public static final ImageDescriptor DESC_FIELD_PROTECTED = 
			createManagedFromKey(ASSIST_PREFIX, IMG_FIELD_PROTECTED);
	public static final ImageDescriptor DESC_FIELD_PRIVATE = 
			createManagedFromKey(ASSIST_PREFIX, IMG_FIELD_PRIVATE);
	public static final ImageDescriptor DESC_FIELD_DEFAULT = 
			createManagedFromKey(ASSIST_PREFIX, IMG_FIELD_DEFAULT);
	
	public static final ImageDescriptor DESC_CLASS_PUBLIC = 
		createManagedFromKey(ASSIST_PREFIX, IMG_CLASS_PUBLIC);
	
	public static final ImageDescriptor DESC_OBJ_PROJECT =
			createManagedFromKey(UNDEFINED, IMG_PROJECT);
	
	public static final ImageDescriptor DESC_OBJ_SOURCE_FOLDER =
		createManagedFromKey(UNDEFINED, IMG_SOURCE_FOLDER);
	
	public static final ImageDescriptor DESC_OBJ_BUILD_FILE =
		createManagedFromKey(UNDEFINED, IMG_BUILD_FILE);
	
	public static final ImageDescriptor DESC_OBJ_PACKAGE =
		createManagedFromKey(UNDEFINED, IMG_PACKAGE);
	
	public static final ImageDescriptor DESC_WIZBAN_NEWPACK = 
			createUnManaged(WIZARDS_PREFIX, "newpack_wiz.png"); 


	private static final class CachedImageDescriptor extends ImageDescriptor {
		private final ImageDescriptor fDescriptor;
		private ImageData fData;

		public CachedImageDescriptor(ImageDescriptor descriptor) {
			fDescriptor = descriptor;
		}

		@Override
		public ImageData getImageData() {
			if (fData == null) {
				fData = fDescriptor.getImageData();
			}
			return fData;
		}
	}

	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key the image's key
	 * @return the image managed under the given key
	 */
	public static Image get(String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * Returns the image descriptor for the given key in this registry. Might be
	 * called in a non-UI thread.
	 * 
	 * @param key the image's key
	 * @return the image descriptor for the given key
	 */
	public static ImageDescriptor getDescriptor(String key) {
		if (fgImageRegistry == null) {
			return fgAvoidSWTErrorMap.get(key);
		}
		return getImageRegistry().getDescriptor(key);
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *tool16 folders.
	 * 
	 * @param action the action
	 * @param iconName the icon name
	 */
	public static void setToolImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "tool16", iconName); //$NON-NLS-1$
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *lcl16 folders.
	 * 
	 * @param action the action
	 * @param iconName the icon name
	 */
	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	/*
	 * Helper method to access the image registry from the JavaPlugin class.
	 */
	static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			fgImageRegistry = new ImageRegistry();
			for (Iterator<String> iter = fgAvoidSWTErrorMap.keySet().iterator(); 
					iter.hasNext();) {
				
				String key = iter.next();
				fgImageRegistry.put(key, fgAvoidSWTErrorMap
						.get(key));
			}
			fgAvoidSWTErrorMap = null;
		}
		return fgImageRegistry;
	}

	// ---- Helper methods to access icons on the file system
	// --------------------------------------

	private static void setImageDescriptors(IAction action, String type,
			String relPath) {
		ImageDescriptor id = create("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);

		/*
		 * id= create("c" + type, relPath, false); //$NON-NLS-1$ if (id != null)
		 * action.setHoverImageDescriptor(id);
		 */

		ImageDescriptor descriptor = create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor);
	}

	private static ImageDescriptor createManagedFromKey(String prefix,
			String key) {
		return createManaged(prefix, key.substring(NAME_PREFIX_LENGTH), key);
	}

	private static ImageDescriptor createManaged(String prefix, String name,
			String key) {
		ImageDescriptor result = create(prefix, name, true);

		if (fgAvoidSWTErrorMap == null) {
			fgAvoidSWTErrorMap = new HashMap<String, ImageDescriptor>();
		}
		fgAvoidSWTErrorMap.put(key, result);
		if (fgImageRegistry != null) {
			EclihxUIPlugin.getLogHelper().logError("Image registry already defined"); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or
	 * <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name,
			boolean useMissingImageDescriptor) {
		
		IPath path = ICONS_PATH.append(prefix).append(name);

		return createImageDescriptor(
				EclihxUIPlugin.getDefault().getBundle(),
				path,
				useMissingImageDescriptor);
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, the 'missing image descriptor' is returned.
	 */
	private static ImageDescriptor createUnManaged(String prefix, String name) {
		return create(prefix, name, true);
	}

	/**
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle and let tye descriptor cache the image data. If no image could be
	 * found, the 'missing image descriptor' is returned.
	 */
	private static ImageDescriptor createUnManagedCached(String prefix, String name) {
		return new CachedImageDescriptor(create(prefix, name, true));
	}

	/**
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found,
	 * <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>. Added for 3.1.1.
	 * @param bundle 
	 * @param path 
	 * @param useMissingImageDescriptor 
	 * @return 
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		
		EclihxUIPlugin.getLogHelper().logError(
				String.format("Can't find %s image.", path.toOSString()));
		
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}
}
