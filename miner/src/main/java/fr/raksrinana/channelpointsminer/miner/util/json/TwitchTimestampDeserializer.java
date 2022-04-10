package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class TwitchTimestampDeserializer extends StdDeserializer<Instant>{
	protected TwitchTimestampDeserializer(){
		this(null);
	}
	
	protected TwitchTimestampDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
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
