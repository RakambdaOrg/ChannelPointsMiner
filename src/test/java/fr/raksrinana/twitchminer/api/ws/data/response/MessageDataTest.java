package fr.raksrinana.twitchminer.api.ws.data.response;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class MessageDataTest{
	private static final String JSON_CONTENT = "json-content";
	
	@Mock
	private Message message;
	
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