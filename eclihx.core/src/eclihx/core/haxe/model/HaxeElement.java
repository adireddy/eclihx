package eclihx.core.haxe.model;

import org.eclipse.core.runtime.PlatformObject;

import eclihx.core.haxe.model.core.IHaxeElement;

/**
 * Common behavior for all Haxe elements.
 */
abstract class HaxeElement extends PlatformObject implements IHaxeElement {

	/**
	 * The parent element of this one.
	 */
	protected IHaxeElement parent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		HaxeElement test = (HaxeElement) obj;

		return getBaseResource().equals(test.getBaseResource());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getBaseResource().hashCode();
	}

	/**
	 * Constructor with parent element.
	 * 
	 * @param parent the parent element. <code>null</code> for root elements.
	 */
	public HaxeElement(IHaxeElement parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eclihx.core.haxe.model.core.IHaxeElement#getParent()
	 */
	@Override
	public final IHaxeElement getParent() {
		return parent;
	}
}
