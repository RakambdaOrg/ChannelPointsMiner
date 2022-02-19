package fr.raksrinana.channelpointsminer.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

public interface IEvent{
	@NotNull
	String getAsLog();
	
	@NotNull
	Webhook getAsWebhookEmbed();
	
	@NotNull
	Webhook getAsWebhookMessage();
	
	@NotNull
	IMiner getMiner();
	
	@NotNull
	Instant getInstant();
}
