/**
 * 
 */
package eclihx.ui.internal.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IElementComparer;

import eclihx.core.haxe.model.core.IHaxeElement;
import eclihx.core.haxe.model.core.IHaxePackage;

/**
 * Make Haxe elements to be comparable with resources.
 */
public class HaxeNavigatorComparer implements IElementComparer {
	
	/**
	 * An instance of the comparer
	 */
	public final static HaxeNavigatorComparer INSTANCE = 
			new HaxeNavigatorComparer();

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean equals(Object a, Object b) {
		
		if (a == null || b == null) {
			return false;
		}
		
		if (a.getClass() == b.getClass()) {
			return a.equals(b);
		}
		
		if (a instanceof IHaxeElement && b instanceof IResource) {
			return equals((IHaxeElement)a, (IResource)b);
		}
		
		if (a instanceof IResource && b instanceof IHaxeElement) {
			return equals((IHaxeElement)b, (IResource)a);
		}
		
		return false;
	}
	
	/**
	 * Compares Haxe element with resource.
	 * @param element the Haxe element.
	 * @param resource the resource object
	 * @return <code>true</code> if Haxe element wraps the given resource.
	 */
	public boolean equals(IHaxeElement element, IResource resource) {
		
		
		if (element instanceof IHaxePackage && 
				((IHaxePackage)element).isDefault()) {
			//TODO 4 fix it. This is a hack. 
			// Default package doesn't have a strict analogy in 
			// the resource world.
			return false;
		}
		return element.getBaseResource().equals(resource);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object element) {
		return element.hashCode();
	}

}
