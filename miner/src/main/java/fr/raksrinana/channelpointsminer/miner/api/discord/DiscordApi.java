package fr.raksrinana.channelpointsminer.miner.api.discord;

import fr.raksrinana.channelpointsminer.miner.api.discord.data.DiscordResponse;
import fr.raksrinana.channelpointsminer.miner.api.discord.data.Webhook;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;

@RequiredArgsConstructor
@Log4j2
public class DiscordApi{
	private static final int MAX_ATTEMPT = 3;
	
	@NotNull
	private final URL webhookUrl;
	@NotNull
	private final UnirestInstance unirest;
	
	@SneakyThrows
	public synchronized void sendMessage(@NotNull Webhook webhook){
		webhook.setUsername("ChannelPointsMiner");
		sendMessage(webhook, 0);
	}
	
	@SneakyThrows
	private synchronized void sendMessage(@NotNull Webhook webhook, int attempt){
		if(attempt >= MAX_ATTEMPT){
			log.error("Failed to send discord message after {} attempts", MAX_ATTEMPT);
			return;
		}
		
		var response = unirest.post(webhookUrl.toString())
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(webhook)
				.asObject(DiscordResponse.class);
		
		if(response.getStatus() == 204){
			return;
		}
		
		if(response.getStatus() == 429){
			var retryAfter = response.getBody().getRetryAfter();
			log.warn("Failed to send discord message, rate limited, retrying in {} milliseconds", retryAfter);
			Thread.sleep(retryAfter + 50);
			sendMessage(webhook, attempt + 1);
		}
		else{
			log.error("Failed to send Discord message, {} => {}", response.getStatus(), response.getBody());
		}
	}
}
