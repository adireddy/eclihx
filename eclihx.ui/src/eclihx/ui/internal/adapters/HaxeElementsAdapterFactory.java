package eclihx.ui.internal.adapters;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import eclihx.core.haxe.model.core.IHaxeElement;

/**
 * Adapts Haxe elements to correspondence resources.
 */
public class HaxeElementsAdapterFactory implements IAdapterFactory {

	/**
	 * Collection of adapter types handled by this factory. 
	 */
	private final static Class<?>[] SUPPORTED_CLASSES = new Class<?>[] {
		IResource.class,
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		
		if (IResource.class.equals(adapterType)) {
			return ((IHaxeElement)adaptableObject).getBaseResource(); 
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class<?>[] getAdapterList() {
		return SUPPORTED_CLASSES;
	}

}
