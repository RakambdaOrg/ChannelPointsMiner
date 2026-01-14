package fr.rakambda.channelpointsminer.miner.util.json;

import kong.unirest.core.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class CookieDeserializer extends StdDeserializer<Cookie>{
	public CookieDeserializer(){
		super(Cookie.class);
	}
	
	@Override
	public Cookie deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		return new Cookie(value);
	}
}
