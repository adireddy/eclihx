package eclihx.ui.internal.ui.editors.hx;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Haxe editor specific string resources storage.
 */
public class HaxeEditorMessages {
	private static final String BUNDLE_NAME = "eclihx.ui.internal.ui.editors.hx.HaxeEditorMessages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Returns the message bundle which contains constructed keys.
	 *
	 * @return the message bundle
	 */
	public static ResourceBundle getBundle() {
		return RESOURCE_BUNDLE;
	}

	private HaxeEditorMessages() {
	}

	/**
	 * Get the string by the key.
	 * @param key the key for search.
	 * @return the resource string correspondent given key or key if resource is absent
	 * 		in the bundle.
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
