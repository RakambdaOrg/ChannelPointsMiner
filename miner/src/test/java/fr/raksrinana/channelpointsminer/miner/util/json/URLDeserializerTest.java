package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class URLDeserializerTest extends DeserializerTest<URL>{
	@Test
	void success() throws MalformedURLException{
		assertThat(deserialize("\"https://test.com/path/to/dir\"")).isEqualTo(new URL("https://test.com/path/to/dir"));
	}
	
	@Test
	void noSchemeAssumesHttps() throws MalformedURLException{
		assertThat(deserialize("\"test.com/path/to/dir\"")).isEqualTo(new URL("https://test.com/path/to/dir"));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Test
	void malformed(){
		assertThat(deserialize("\"http://example.com:-80/\"")).isNull();
	}
	
	@Override
	protected JsonDeserializer<URL> getDeserializer(){
		return new URLDeserializer();
	}
}