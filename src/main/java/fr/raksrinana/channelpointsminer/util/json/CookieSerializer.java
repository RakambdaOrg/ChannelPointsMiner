package fr.raksrinana.channelpointsminer.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import kong.unirest.Cookie;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class CookieSerializer extends StdSerializer<Cookie>{
	public CookieSerializer(){
		this(null);
	}
	
	protected CookieSerializer(Class<Cookie> vc){
		super(vc);
	}
	
	@Override
	public void serialize(@NotNull Cookie cookie, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(cookie.toString());
	}
}
