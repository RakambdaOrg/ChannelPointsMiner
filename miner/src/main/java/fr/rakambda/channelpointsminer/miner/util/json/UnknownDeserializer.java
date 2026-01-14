package fr.rakambda.channelpointsminer.miner.util.json;

import lombok.extern.log4j.Log4j2;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

@Log4j2
public class UnknownDeserializer extends StdDeserializer<String>{
	public UnknownDeserializer(){
		super(String.class);
	}
	
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var currentLocation = p.currentLocation();
		var treeNode = p.readValueAsTree();
		var treeNodeStr = treeNode == null ? null : treeNode.toString();
		log.warn("Got actual value for unknown field {} (l:{},c:{}) : {}", p.currentName(), currentLocation.getLineNr(), currentLocation.getColumnNr(), treeNodeStr);
		return treeNodeStr;
	}
}
