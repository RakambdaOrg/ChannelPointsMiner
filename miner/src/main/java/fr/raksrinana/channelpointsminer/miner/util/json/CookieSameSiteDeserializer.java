package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Objects;

public class CookieSameSiteDeserializer extends StdDeserializer<String>{
	public CookieSameSiteDeserializer(){
		this(null);
	}
	
	protected CookieSameSiteDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
		var value = p.getValueAsString();
		if(Objects.isNull(value) || value.isBlank()){
			return null;
		}
		if(Objects.equals("no_restriction", value)){
			return "None";
		}
		return value;
	}
}
