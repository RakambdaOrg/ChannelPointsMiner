package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantSerializer extends StdSerializer<Instant>{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_INSTANT;
	
	public InstantSerializer(){
		this(null);
	}
	
	public InstantSerializer(Class<Instant> t){
		super(t);
	}
	
	@Override
	public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException{
		gen.writeString(DF.format(value));
	}
}
