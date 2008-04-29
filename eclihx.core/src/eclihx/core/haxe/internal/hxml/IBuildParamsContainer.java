package eclihx.core.haxe.internal.hxml;

public interface IBuildParamsContainer {
	
	enum TargetPlatform {
		Flash,
		AS,
		Neko
	}
	
	String getOutputFileName();
	void setOutputFileName(String fileName);
	
	TargetPlatform getTargetPlatform();
	void setTargetPlatform(TargetPlatform platform);
	
	void merge(IBuildParamsContainer container);
	
}
