package fr.rakambda.channelpointsminer.miner.util.json;

import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import kong.unirest.core.Cookie;
import tools.jackson.databind.ValueDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class CookieDeserializerTest extends DeserializerTest<Cookie>{
	@ParameterizedTest
	@ValueSource(strings = {
			"yummy_cookie=choco",
			"id=a3fWa; Expires=Thu, 31 Oct 2021 07:28:00 GMT;",
			"id=a3fWa; Expires=Thu, 21 Oct 2021 07:28:00 GMT; Secure; HttpOnly",
	})
	void stringValue(String content){
		assertThat(deserialize("\"%s\"".formatted(content)))
				.usingRecursiveComparison()
				.isEqualTo(new Cookie(content));
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected ValueDeserializer<Cookie> getDeserializer(){
		return new CookieDeserializer();
	}
}