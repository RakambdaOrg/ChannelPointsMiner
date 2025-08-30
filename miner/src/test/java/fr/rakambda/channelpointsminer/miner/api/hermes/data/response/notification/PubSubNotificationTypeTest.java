package fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class PubSubNotificationTypeTest{
	private static final String JSON_CONTENT = "json-content";
	
	@Mock
	private IPubSubMessage message;
	
	@Test
	void setMessage(){
		try(var jacksonUtils = Mockito.mockStatic(JacksonUtils.class)){
			jacksonUtils.when(() -> JacksonUtils.read(eq(JSON_CONTENT), any())).thenReturn(message);
			
			var tested = PubSubNotificationType.builder().build();
			
			assertDoesNotThrow(() -> tested.setPubsub(JSON_CONTENT));
			assertThat(tested.getPubsub()).isEqualTo(message);
			
			jacksonUtils.verify(() -> JacksonUtils.read(eq(JSON_CONTENT), any()));
		}
	}
}