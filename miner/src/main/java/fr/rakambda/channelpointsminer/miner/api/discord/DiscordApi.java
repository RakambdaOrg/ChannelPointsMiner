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
	private static final int MAX_ATTEMPT = 3;
	
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
			var retryAfter = 0;
			for(var i = 0; i < MAX_ATTEMPT; i++){
				if(retryAfter > 0){
					log.warn("Failed to send discord message, rate limited, retrying in {} milliseconds", retryAfter);
				}
				retryAfter = CompletableFuture.supplyAsync(() -> webhook, CompletableFuture.delayedExecutor(retryAfter, TimeUnit.MILLISECONDS))
						.thenCompose(body -> unirest.post(webhookUrl.toString())
								.header(CONTENT_TYPE, APPLICATION_JSON.toString())
								.body(body)
								.asObjectAsync(DiscordResponse.class)
						)
						.thenApply(response -> {
							if(response.getStatus() == 204){
								return 0;
							}
							
							if(response.getStatus() == 429){
								return response.getBody().getRetryAfter();
							}
							
							log.error("Failed to send Discord message, {} => {}", response.getStatus(), response.getBody());
							return -1;
						})
						.get(1, TimeUnit.MINUTES);
				if(retryAfter <= 0){
					break;
				}
			}
		}
		finally{
			semaphore.release();
		}
	}
}
