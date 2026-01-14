package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class SecondsTimestampDeserializer extends StdDeserializer<Instant>{
	
	public static final BigDecimal MILLISECONDS_IN_SECOND = new BigDecimal(1000);
	
	protected SecondsTimestampDeserializer(){
		super(Instant.class);
	}
	
	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		return Optional.ofNullable(p.getValueAsString())
				.filter(n -> !n.isBlank())
				.map(BigDecimal::new)
				.map(n -> n.multiply(MILLISECONDS_IN_SECOND))
				.map(BigDecimal::longValue)
				.map(Instant::ofEpochMilli)
				.orElse(null);
	}
}
