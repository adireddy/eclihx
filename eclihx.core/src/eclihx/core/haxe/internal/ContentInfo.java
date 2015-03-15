package eclihx.core.haxe.internal;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Objects of this class will store info which was got from the
 * Haxe tips processor. Lately this info will be used in 
 * generation CompletionProposal. 
 */
@XmlRootElement(name = "i")
public class ContentInfo {

	/**
	 * Compare two content infos according to their names.
	 */
	private static class NameComparator implements Comparator<ContentInfo> {
		@Override
		public int compare(ContentInfo arg0, ContentInfo arg1) {
			return arg0.name.compareTo(arg1.name);
		}
	}
	
	/**
	 * Separator of the complex types like functions.
	 */
	public final static String COMPLEX_TYPES_SEPARATOR = "->";
	
	private String name = "";
	private String type = "";
	private String doc = "";
	
	/**
	 * Get the name.
	 * @return the info name.
	 */
	@XmlAttribute(name="n")
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * @param name the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the type.
	 * @return the type.
	 */
	@XmlElement(name="t")
	public String getType() {
		return type;
	}

	/**
	 * Set the type.
	 * 
	 * @param type the type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the Haxe documentation.
	 * 
	 * @return the Haxe documentation string.
	 */
	// TODO 2 check that this is really Haxe doc.
	@XmlElement(name="d")
	public String getDoc() {
		return doc;
	}

	/**
	 * Set the Haxe documentation string.
	 * 
	 * @param haxeDoc the documentation to set.
	 */
	public void setDoc(String haxeDoc) {
		this.doc = haxeDoc;
	}

	/**
	 * Compares informations object according to their names.
	 * 
	 * @return Comparator based on the informations names.
	 */
	public static Comparator<ContentInfo> getNameComparator() {
		return new NameComparator();
	}
	
	/**
	 * Checks if current info contains description for the function.
	 * 
	 * @return <code>true</code> if current info 
	 * contains description for the function.
	 */
	public boolean isFunction() {
		return (!name.isEmpty() && type.indexOf(COMPLEX_TYPES_SEPARATOR) != -1);
	}
	
	/**
	 * Checks if current info contains description of the package.
	 * 
	 * @return <code>true</code> if current info contains description of the package. 
	 */
	public boolean isPackage() {
		// The type string is empty and name first char has lower case.
		return (type.isEmpty() && !name.isEmpty() && Character.isLowerCase(name.charAt(0)));
	}
	
	/**
	 * Checks if current info contains description of the class name.
	 * 
	 * @return <code>true</code> if current info contains description of the class name.
	 */
	public boolean isClassName() {
		// The type string is empty and name first char has upper case.
		return (type.isEmpty() && !name.isEmpty() && Character.isUpperCase(name.charAt(0)));
	}

	/**
	 * Checks if current object contains variable information.
	 * 
	 * @return <code>true</code> if current info describes a variable.
	 */
	public boolean isVariable() {
		return !(type.isEmpty() || name.isEmpty());
	}
	
	/**
	 * Checks if current object contains a function call description.
	 * 
	 * @return <code>true</code> if current info stores a call description.
	 */
	public boolean isCallDescription() {
		return (name.isEmpty() && type.indexOf(COMPLEX_TYPES_SEPARATOR) != -1);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ContentInfo: name=%s; type=%s; doc=%s.", name, type, doc);
	}
	
	
}