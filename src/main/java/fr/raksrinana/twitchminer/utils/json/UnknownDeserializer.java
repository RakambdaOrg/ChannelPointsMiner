package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;

@Log4j2
public class UnknownDeserializer extends StdDeserializer<Object>{
	public UnknownDeserializer(){
		this(null);
	}
	
	protected UnknownDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	@Nullable
	public Object deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		var currentLocation = jsonParser.getCurrentLocation();
		log.warn("Got actual value for field {} (l:{},c:{}) : {}", jsonParser.getCurrentName(), currentLocation.getLineNr(), currentLocation.getColumnNr(), jsonParser.readValueAsTree().toString());
		return jsonParser.getValueAsString();
	}
}
