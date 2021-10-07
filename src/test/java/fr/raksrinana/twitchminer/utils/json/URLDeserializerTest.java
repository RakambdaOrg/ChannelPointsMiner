package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

class URLDeserializerTest extends DeserializerTest<URL>{
	@Override
	protected JsonDeserializer<URL> getDeserializer(){
		return new URLDeserializer();
	}
	
	@Test
	void success() throws MalformedURLException{
		assertThat(deserialize("\"https://test.com/path/to/dir\"")).isEqualTo(new URL("https://test.com/path/to/dir"));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Test
	void malformed(){
		assertThat(deserialize("\"http://example.com:-80/\"")).isNull();
	}
}