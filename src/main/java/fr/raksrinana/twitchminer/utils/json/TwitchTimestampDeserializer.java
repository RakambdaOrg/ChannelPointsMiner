package fr.raksrinana.twitchminer.utils.json;

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
		var rawValue = BigDecimal.valueOf(p.getValueAsDouble());
		var seconds = rawValue.intValue();
		return Instant.ofEpochSecond(seconds);
	}
}
