package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class UnknownDeserializerTest extends DeserializerTest<String>{
	@Override
	protected JsonDeserializer<String> getDeserializer(){
		return new UnknownDeserializer();
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"\"https://test.com/path/to/dir\"",
			"\"\""
	})
	void stringValue(String content){
		assertThat(deserialize(content)).isEqualTo(content);
	}
	
	@Test
	void nestedJsonValue(){
		assertThat(deserialize("""
				{
					"innerKey": 25
				}
				""")).isEqualTo("{\"innerKey\":25}");
	}
}