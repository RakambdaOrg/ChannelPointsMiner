package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class DeserializerTest<T>{
	private ObjectMapper mapper;
	
	protected abstract JsonDeserializer<T> getDeserializer();
	
	@BeforeEach
	void setUp(){
		mapper = new ObjectMapper();
	}
	
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
		
		try(var stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))){
			var parser = mapper.getFactory().createParser(stream);
			var ctxt = mapper.getDeserializationContext();
			
			parser.nextToken();
			parser.nextToken();
			parser.nextToken();
			
			return getDeserializer().deserialize(parser, ctxt);
		}
	}
}
