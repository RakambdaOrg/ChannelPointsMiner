package fr.rakambda.channelpointsminer.miner.api.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.data.DiscordResponse;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;

@RequiredArgsConstructor
@Log4j2
public class DiscordApi{
	@NotNull
	private final URL webhookUrl;
	@NotNull
	private final UnirestInstance unirest;
	@NotNull
	private final Semaphore semaphore = new Semaphore(1, true);
	
	@SneakyThrows
	public void sendMessage(@NotNull Webhook webhook){
		webhook.setUsername("ChannelPointsMiner");
		semaphore.acquire();
		try{
			var response = unirest.post(webhookUrl.toString())
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.body(webhook)
					.asObject(DiscordResponse.class);
			
			if(response.getStatus() != 204){
				log.error("Failed to send Discord message, {} => {}", response.getStatus(), response.getBody());
			}
		}
		finally{
			semaphore.release();
		}
	}
}
