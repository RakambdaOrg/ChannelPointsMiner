package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jspecify.annotations.NonNull;
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
	public void serialize(@NonNull URL url, @NonNull JsonGenerator jsonGenerator, @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(url.toString());
	}
}
