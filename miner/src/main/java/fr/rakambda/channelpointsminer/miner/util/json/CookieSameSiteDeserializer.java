package fr.rakambda.channelpointsminer.miner.util.json;

import kong.unirest.core.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.util.Objects;

public class CookieSameSiteDeserializer extends StdDeserializer<String>{
	public CookieSameSiteDeserializer(){
		super(Cookie.class);
	}
	
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsString();
		if(Objects.equals("no_restriction", value)){
			return "None";
		}
		return "Lax";
	}
}
