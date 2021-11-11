package fr.raksrinana.channelpointsminer.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URL;

public class URLSerializer extends StdSerializer<URL>{
	public URLSerializer(){
		this(null);
	}
	
	protected URLSerializer(Class<URL> t){
		super(t);
	}
	
	@Override
	public void serialize(@NotNull URL url, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(url.toString());
	}
}
