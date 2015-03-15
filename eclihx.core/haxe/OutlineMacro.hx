import haxe.macro.Expr;
import haxe.macro.Context;
import haxe.macro.Type;

//-cp src
//--no-output 
//-swf fake.swf 
//-main OutlineMacro 
//--macro OutlineMacro.traceClassInfo()

class OutlineMacro {	
	public static function main() {	}

	@:macro public static function traceClassInfo() {
	
		function getFieldXml(fieldName: String, field: ClassField) {
			var fieldNode = Xml.createElement(fieldName);
			fieldNode.set("name", field.name);
			fieldNode.set("isPublic", Std.string(field.isPublic));
			switch (field.kind) {
				case FieldKind.FMethod(_):
					fieldNode.set("type", "method");
				case FieldKind.FVar(_, _):
					fieldNode.set("type", "field");
			}		
			
			return fieldNode;
		} 

		function getTypeXml(type: Type) : Xml {
			var typeNode = Xml.createElement("type");
			
			switch (type) {
				case TInst(classTypeRef, _):
					typeNode.set("name", classTypeRef.get().name);
					
					var fieldsNode = Xml.createElement("members");
					typeNode.addChild(fieldsNode);
				
					for (field in classTypeRef.get().fields.get()) {
						fieldsNode.addChild(getFieldXml("member", field));
	    			}
	    			
	    			var staticsNode = Xml.createElement("statics");
	    			typeNode.addChild(staticsNode);
	    			
	    			for (field in classTypeRef.get().statics.get()) {
	    				staticsNode.addChild(getFieldXml("static", field));
	    			}
	    			
	    			var genericParamsNode = Xml.createElement("typeparams");
	    			typeNode.addChild(genericParamsNode);
					
					// Need a way to read a parameter name
					for (typeParam in classTypeRef.get().params) {
						var typeParamNode = Xml.createElement("param");
						typeParamNode.set("name", typeParam.name);
						genericParamsNode.addChild(typeParamNode);
					}
				default:
					trace("Not finished yet");
					trace(type);
			}
			
			return typeNode;
		};
		
		function getModuleXml(moduleName: String) : Xml {
			var moduleNode = Xml.createElement("module");
			moduleNode.set("file", moduleName);
			
			for (type in Context.getModule(moduleName)) {
				moduleNode.addChild(getTypeXml(type));
			}
			
			return moduleNode;
		}
		
		var xmlDocument = Xml.createDocument();
		xmlDocument.addChild(getModuleXml("flash.display.Graphics"));
		
		var file = neko.io.File.write("outline.xml", false);
		file.writeString(xmlDocument.toString());
		file.close();
		
		return null;
    }
}