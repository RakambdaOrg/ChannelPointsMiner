package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class SecondsTimestampDeserializer extends StdDeserializer<Instant>{
	
	public static final BigDecimal MILLISECONDS_IN_SECOND = new BigDecimal(1000);
	
	protected SecondsTimestampDeserializer(){
		this(null);
	}
	
	protected SecondsTimestampDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		return Optional.ofNullable(p.getValueAsString())
				.filter(n -> !n.isBlank())
				.map(BigDecimal::new)
				.map(n -> n.multiply(MILLISECONDS_IN_SECOND))
				.map(BigDecimal::longValue)
				.map(Instant::ofEpochMilli)
				.orElse(null);
	}
}
