package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class MillisecondsTimestampDeserializerTest extends DeserializerTest<Instant>{
	@Test
	void longValue(){
		assertThat(deserialize("%d".formatted(1633717410096L))).isEqualTo(Instant.parse("2021-10-08T18:23:30.096000Z"));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected JsonDeserializer<Instant> getDeserializer(){
		return new MillisecondsTimestampDeserializer();
	}
}