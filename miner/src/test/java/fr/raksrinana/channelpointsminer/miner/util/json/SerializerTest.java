package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import java.io.IOException;
import java.io.StringWriter;

public abstract class SerializerTest<T>{
	@SneakyThrows({
			IOException.class
	})
	public String serialize(T object){
		var jsonWriter = new StringWriter();
		var jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
		var serializerProvider = new ObjectMapper().getSerializerProvider();
		getSerializer().serialize(object, jsonGenerator, serializerProvider);
		jsonGenerator.flush();
		return jsonWriter.toString();
	}
	
	protected abstract JsonSerializer<T> getSerializer();
}
