package fr.raksrinana.twitchminer.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import kong.unirest.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

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
	protected JsonDeserializer<Cookie> getDeserializer(){
		return new CookieDeserializer();
	}
}