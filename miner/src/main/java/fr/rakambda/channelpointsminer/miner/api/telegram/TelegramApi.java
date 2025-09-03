package fr.rakambda.channelpointsminer.miner.api.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.api.telegram.data.TelegramResponse;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Optional;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;

@RequiredArgsConstructor
@Log4j2
public class TelegramApi{
	private static final int MAX_ATTEMPT = 3;
	
	@NonNull
	private final UnirestInstance unirest;
	
	@SneakyThrows
	public synchronized void sendMessage(@NonNull Message message){
		sendMessage(message, 0);
	}
	
	@SneakyThrows
	private synchronized void sendMessage(@NonNull Message message, int attempt){
		if(attempt >= MAX_ATTEMPT){
			log.error("Failed to send telegram message after {} attempts", MAX_ATTEMPT);
			return;
		}
		
		var response = unirest.post("/sendMessage")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(message)
				.asObject(TelegramResponse.class);
		
		if(!response.isSuccess() || !response.getBody().isSuccess()){
			log.warn("Failed to send telegram message, {}", Optional.ofNullable(response.getBody()).map(TelegramResponse::getDescription).orElse(null));
			sendMessage(message, attempt + 1);
		}
	}
}
