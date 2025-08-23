package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticateResponseTest{
	@Test
	void noError(){
		var tested = AuthenticateResponse.builder()
				.authenticateResponse(AuthenticateResponse.AuthenticateResponseData.builder()
						.error("ok")
						.build())
				.build();
		assertThat(tested.hasError()).isFalse();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {
			"    ",
			"error",
			"no"
	})
	void hasError(String error){
		var tested = AuthenticateResponse.builder()
				.authenticateResponse(AuthenticateResponse.AuthenticateResponseData.builder()
						.error(error)
						.build())
				.build();
		
		assertThat(tested.hasError()).isTrue();
	}
}