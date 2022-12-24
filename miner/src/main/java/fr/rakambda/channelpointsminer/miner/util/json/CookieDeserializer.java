package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kong.unirest.core.Cookie;
import java.io.IOException;

public class CookieDeserializer extends StdDeserializer<Cookie>{
	public CookieDeserializer(){
		this(null);
	}
	
	protected CookieDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public Cookie deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		var value = p.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		return new Cookie(value);
	}
}
