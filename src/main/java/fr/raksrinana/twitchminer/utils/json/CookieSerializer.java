package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import kong.unirest.Cookie;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class CookieSerializer extends JsonSerializer<Cookie>{
	@Override
	public void serialize(@NotNull Cookie cookie, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(cookie.toString());
	}
}
