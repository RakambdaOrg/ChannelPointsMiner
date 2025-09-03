package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import kong.unirest.core.Cookie;
import org.jspecify.annotations.NonNull;
import java.io.IOException;

public class CookieSerializer extends StdSerializer<Cookie>{
	public CookieSerializer(){
		this(null);
	}
	
	protected CookieSerializer(Class<Cookie> vc){
		super(vc);
	}
	
	@Override
	public void serialize(@NonNull Cookie cookie, @NonNull JsonGenerator jsonGenerator, @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(cookie.toString());
	}
}
