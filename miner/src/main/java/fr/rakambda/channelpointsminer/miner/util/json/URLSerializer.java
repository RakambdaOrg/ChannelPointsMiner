package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import java.net.URL;

public class URLSerializer extends StdSerializer<URL>{
	public URLSerializer(){
		super(URL.class);
	}
	
	@Override
	public void serialize(URL value, JsonGenerator gen, SerializationContext provider) throws JacksonException{
		gen.writeString(value.toString());
	}
}
