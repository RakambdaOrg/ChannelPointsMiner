package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonSerializer;
import kong.unirest.core.Cookie;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CookieSerializerTest extends SerializerTest<Cookie>{
	public static Stream<Arguments> generateCases(){
		return Stream.of(
				arguments(new Cookie("yummy_cookie=choco"), "yummy_cookie=choco"),
				arguments(new Cookie("id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT"), "id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT"),
				arguments(new Cookie("id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT;"), "id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT"),
				arguments(new Cookie("id=a3fWa;Expires=Thu, 21 Oct 2021 07:28:00 GMT;"), "id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT"),
				arguments(new Cookie("id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT;HttpOnly;Secure"), "id=a3fWa;Expires=Thu, 21-Oct-2021 07:28:00 GMT;HttpOnly;Secure")
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateCases")
	void stringValue(Cookie cookie, String expected){
		assertThat(serialize(cookie)).isEqualTo("\"%s\"".formatted(expected));
	}
	
	@Override
	protected JsonSerializer<Cookie> getSerializer(){
		return new CookieSerializer();
	}
}