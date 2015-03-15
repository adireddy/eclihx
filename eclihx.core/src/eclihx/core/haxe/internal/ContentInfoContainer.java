package eclihx.core.haxe.internal;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The only purpose of this class is to simplify working with tips-xml output.
 */
@XmlRootElement(name = "list")
public class ContentInfoContainer {

	/**
	 * Group of tips for the defined position in the Haxe code.
	 */
	@XmlElement(name = "i")
	public ArrayList<ContentInfo> contentInfos = new ArrayList<ContentInfo>();
}
