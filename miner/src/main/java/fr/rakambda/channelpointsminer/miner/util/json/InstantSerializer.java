package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantSerializer extends StdSerializer<Instant>{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_INSTANT;
	
	public InstantSerializer(){
		super(Instant.class);
	}
	
	@Override
	public void serialize(Instant value, JsonGenerator gen, SerializationContext provider) throws JacksonException{
		gen.writeString(DF.format(value));
	}
}
