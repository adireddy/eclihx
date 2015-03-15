package eclihx.core.haxe.internal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Objects of this class will store info which was got from the
 * Haxe tips processor for function call.  
 */
@XmlRootElement(name = "root")
public class FunctionSignature {

	private String fullType; 
	
	/**
	 * Get the full type of the function.
	 * @return the function full type.
	 */
	@XmlElement(name = "type")
	public String getFullType() {
		return fullType;
	}
	
	/**
	 * Set the function full type.
	 * @param functionFullType the full function type string.
	 */
	public void setFullType(String functionFullType) {
		this.fullType = functionFullType.trim();
	}

	/**
	 * Default constructor for serialization.
	 */
	public FunctionSignature() {}
}
