package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.time.ZonedDateTime;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class ISO8601ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime>{
	protected ISO8601ZonedDateTimeDeserializer(){
		super(ZonedDateTime.class);
	}
	
	@Override
	public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		return ZonedDateTime.parse(value, ISO_DATE_TIME);
	}
}
