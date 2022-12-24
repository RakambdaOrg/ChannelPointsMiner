package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class UnknownDeserializerTest extends DeserializerTest<String>{
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
	
	@Override
	protected JsonDeserializer<String> getDeserializer(){
		return new UnknownDeserializer();
	}
}