package fr.rakambda.channelpointsminer.miner.api.ws.data.response;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class MessageDataTest{
	private static final String JSON_CONTENT = "json-content";
	
	@Mock
	private IPubSubMessage message;
	
	@Test
	void setMessage(){
		try(var jacksonUtils = Mockito.mockStatic(JacksonUtils.class)){
			jacksonUtils.when(() -> JacksonUtils.read(eq(JSON_CONTENT), any())).thenReturn(message);
			
			var tested = MessageData.builder().build();
			
			assertDoesNotThrow(() -> tested.setMessage(JSON_CONTENT));
			assertThat(tested.getMessage()).isEqualTo(message);
			
			jacksonUtils.verify(() -> JacksonUtils.read(eq(JSON_CONTENT), any()));
		}
	}
}