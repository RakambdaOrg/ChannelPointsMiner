package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonSerializer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class URLSerializerTest extends SerializerTest<URL>{
	@Test
	void stringValue() throws MalformedURLException{
		var content = "https://google.com/";
		assertThat(serialize(URI.create(content).toURL())).isEqualTo("\"%s\"".formatted(content));
	}
	
	@Override
	protected JsonSerializer<URL> getSerializer(){
		return new URLSerializer();
	}
}