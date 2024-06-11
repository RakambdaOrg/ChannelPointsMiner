package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;

@Log4j2
public class UnknownDeserializer extends StdDeserializer<String>{
	public UnknownDeserializer(){
		this(null);
	}
	
	protected UnknownDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	@Nullable
	public String deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		var currentLocation = jsonParser.currentLocation();
		var treeNode = jsonParser.readValueAsTree();
		var treeNodeStr = treeNode == null ? null : treeNode.toString();
		log.warn("Got actual value for unknown field {} (l:{},c:{}) : {}", jsonParser.currentName(), currentLocation.getLineNr(), currentLocation.getColumnNr(), treeNodeStr);
		return treeNodeStr;
	}
}
