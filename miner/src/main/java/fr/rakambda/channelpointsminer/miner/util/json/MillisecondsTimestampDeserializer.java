package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;

public class MillisecondsTimestampDeserializer extends StdDeserializer<Instant>{
	protected MillisecondsTimestampDeserializer(){
		this(null);
	}
	
	protected MillisecondsTimestampDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		var value = p.getValueAsLong(-1);
		if(value < 0){
			return null;
		}
		return Instant.ofEpochMilli(value);
	}
}
