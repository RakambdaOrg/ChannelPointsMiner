package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonSerializer;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class InstantSerializerTest extends SerializerTest<Instant>{
	@Test
	void stringValue(){
		var content = "2021-10-15T12:52:32.123Z";
		assertThat(serialize(Instant.parse(content))).isEqualTo("\"%s\"".formatted(content));
	}
	
	@Override
	protected JsonSerializer<Instant> getSerializer(){
		return new InstantSerializer();
	}
}