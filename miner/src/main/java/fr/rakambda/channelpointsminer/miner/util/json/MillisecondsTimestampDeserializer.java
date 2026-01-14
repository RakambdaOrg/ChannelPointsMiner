package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.time.Instant;

public class MillisecondsTimestampDeserializer extends StdDeserializer<Instant>{
	protected MillisecondsTimestampDeserializer(){
		super(Instant.class);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsLong(-1);
		if(value < 0){
			return null;
		}
		return Instant.ofEpochMilli(value);
	}
}
