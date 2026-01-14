package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.math.BigDecimal;
import java.time.Instant;

public class TwitchTimestampDeserializer extends StdDeserializer<Instant>{
	protected TwitchTimestampDeserializer(){
		super(Instant.class);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		var number = new BigDecimal(value);
		var seconds = number.intValue();
		var nanos = number.remainder(BigDecimal.ONE).movePointRight(9).longValue();
		return Instant.ofEpochSecond(seconds).plusNanos(nanos);
	}
}
