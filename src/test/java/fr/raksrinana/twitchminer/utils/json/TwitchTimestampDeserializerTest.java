package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class TwitchTimestampDeserializerTest extends DeserializerTest<Instant>{
	@Override
	protected JsonDeserializer<Instant> getDeserializer(){
		return new TwitchTimestampDeserializer();
	}
	
	@Test
	void stringValue(){
		assertThat(deserialize("\"%s\"".formatted("1633717410.096392"))).isEqualTo(Instant.parse("2021-10-08T18:23:30.096392Z"));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
}