package fr.rakambda.channelpointsminer.miner.util.json;

import lombok.extern.log4j.Log4j2;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;
import static java.util.Objects.nonNull;

@Log4j2
public class URLDeserializer extends StdDeserializer<URL>{
	private static final Pattern SCHEME_PATTERN = Pattern.compile("^\\w+://.*");
	
	public URLDeserializer(){
		super(URL.class);
	}
	
	@Override
	public URL deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		try{
			var value = p.getValueAsString();
			if(nonNull(value) && !value.isBlank()){
				if(!SCHEME_PATTERN.matcher(value).matches()){
					value = "https://" + value;
				}
				return URI.create(value).toURL();
			}
		}
		catch(MalformedURLException | IllegalArgumentException e){
			log.warn("Failed to parse URL: {} at {} in {}", p.getValueAsString(), p.currentName(), p.currentLocation(), e);
		}
		return null;
	}
}
