package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import kong.unirest.Cookie;
import java.io.IOException;

public class CookieDeserializer extends JsonDeserializer<Cookie>{
	@Override
	public Cookie deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		return new Cookie(p.getValueAsString());
	}
}
