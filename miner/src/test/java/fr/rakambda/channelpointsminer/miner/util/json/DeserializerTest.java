package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.SneakyThrows;
import tools.jackson.databind.ValueDeserializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class DeserializerTest<T> extends JacksonTest{
	@SneakyThrows({
			JsonParseException.class,
			IOException.class
	})
	public T deserialize(String innerJson){
		String json = """
				{
				    "value": %s
				}
				""".formatted(innerJson);
		
		try(var stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
				var parser = getMapper().reader().createParser(stream)){
			var ctxt = getMapper()._deserializationContext();
			
			parser.nextToken();
			parser.nextToken();
			parser.nextToken();
			
			return getDeserializer().deserialize(parser, ctxt);
		}
	}
	
	protected abstract ValueDeserializer<T> getDeserializer();
}
