package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

class URLSerializerTest extends SerializerTest<URL>{
	@Test
	void stringValue() throws MalformedURLException{
		var content = "https://google.com/";
		assertThat(serialize(new URL(content))).isEqualTo("\"%s\"".formatted(content));
	}
	
	@Override
	protected JsonSerializer<URL> getSerializer(){
		return new URLSerializer();
	}
}