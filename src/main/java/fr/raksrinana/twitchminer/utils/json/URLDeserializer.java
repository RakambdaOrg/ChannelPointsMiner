package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import static java.util.Objects.nonNull;

@Log4j2
public class URLDeserializer extends StdDeserializer<URL>{
	public URLDeserializer(){
		this(null);
	}
	
	protected URLDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	@Nullable
	public URL deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		try{
			var value = jsonParser.getValueAsString();
			if(nonNull(value) && !value.isBlank() && !value.equals(".")){
				return new URL(value);
			}
		}
		catch(MalformedURLException e){
			log.warn("Failed to parse URL: {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
