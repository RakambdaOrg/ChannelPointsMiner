package fr.raksrinana.channelpointsminer.miner.api.ws.data.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseResponseTest{
	@Test
	void hasError(){
		var tested = ResponseResponse.builder().error("I'm an error").build();
		assertThat(tested.hasError()).isTrue();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"    "})
	void noError(String error){
		var tested = ResponseResponse.builder().error(error).build();
		assertThat(tested.hasError()).isFalse();
	}
}