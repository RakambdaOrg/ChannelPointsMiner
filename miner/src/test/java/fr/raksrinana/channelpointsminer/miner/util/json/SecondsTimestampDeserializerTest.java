package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class SecondsTimestampDeserializerTest extends DeserializerTest<Instant>{
	@Test
	void doubleValue(){
		assertThat(deserialize("1633717410.096000")).isEqualTo(Instant.parse("2021-10-08T18:23:30.096000Z"));
	}
	
	@Test
	void integerValue(){
		assertThat(deserialize("1633717410")).isEqualTo(Instant.parse("2021-10-08T18:23:30.000Z"));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected JsonDeserializer<Instant> getDeserializer(){
		return new SecondsTimestampDeserializer();
	}
}