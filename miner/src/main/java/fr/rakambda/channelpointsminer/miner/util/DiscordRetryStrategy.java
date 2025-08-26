package fr.rakambda.channelpointsminer.miner.util;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.RetryStrategy;
import lombok.extern.log4j.Log4j2;
import java.time.Duration;

@Log4j2
public class DiscordRetryStrategy extends RetryStrategy.Standard{
	public DiscordRetryStrategy(int maxAttempts){
		super(maxAttempts);
	}
	
	@Override
	public long getWaitTime(HttpResponse response){
		var delay = super.getWaitTime(response);
		log.info("Discord API call delayed for {}", Duration.ofMillis(delay));
		return delay;
	}
}
