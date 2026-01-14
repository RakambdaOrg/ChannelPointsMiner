package fr.rakambda.channelpointsminer.miner.util.json;

import kong.unirest.core.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class CookieSerializer extends StdSerializer<Cookie>{
	public CookieSerializer(){
		super(Cookie.class);
	}
	
	@Override
	public void serialize(Cookie value, JsonGenerator gen, SerializationContext provider) throws JacksonException{
		gen.writeString(value.toString());
	}
}
