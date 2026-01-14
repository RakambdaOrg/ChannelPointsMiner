package fr.rakambda.channelpointsminer.miner.util.json;

import tools.jackson.databind.ValueSerializer;
import java.io.StringWriter;

public abstract class SerializerTest<T> extends JacksonTest{
	public String serialize(T object){
		var jsonWriter = new StringWriter();
		var jsonGenerator = getMapper().writer().createGenerator(jsonWriter);
		var serializerProvider = getMapper()._serializationContext();
		getSerializer().serialize(object, jsonGenerator, serializerProvider);
		jsonGenerator.flush();
		return jsonWriter.toString();
	}
	
	protected abstract ValueSerializer<T> getSerializer();
}
