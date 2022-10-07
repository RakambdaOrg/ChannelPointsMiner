package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class SecondsTimestampDeserializer extends StdDeserializer<Instant>{
	protected SecondsTimestampDeserializer(){
		this(null);
	}
	
	protected SecondsTimestampDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		var value = p.getValueAsDouble(-1D);
		if(value < 0){
			return null;
		}
		var bigDecimal = new BigDecimal(value);
		return Instant.ofEpochSecond(bigDecimal.longValue());
	}
}
