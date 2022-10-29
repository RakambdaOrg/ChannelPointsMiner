package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ParallelizableTest
class ISO8601ZonedDateTimeDeserializerTest extends DeserializerTest<ZonedDateTime>{
	public static Stream<Arguments> generateCases(){
		return Stream.of(
				arguments("2021-05-25T14:36:18.123456789Z", ZonedDateTime.of(2021, 5, 25, 14, 36, 18, 123456789, UTC)),
				arguments("2021-05-25T14:36:18.123456Z", ZonedDateTime.of(2021, 5, 25, 14, 36, 18, 123456000, UTC)),
				arguments("2021-05-25T14:36:18.123Z", ZonedDateTime.of(2021, 5, 25, 14, 36, 18, 123000000, UTC)),
				arguments("2021-05-25T14:36:18Z", ZonedDateTime.of(2021, 5, 25, 14, 36, 18, 0, UTC))
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateCases")
	void stringValue(String content, ZonedDateTime expected){
		assertThat(deserialize("\"%s\"".formatted(content))).isEqualTo(expected);
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected JsonDeserializer<ZonedDateTime> getDeserializer(){
		return new ISO8601ZonedDateTimeDeserializer();
	}
}