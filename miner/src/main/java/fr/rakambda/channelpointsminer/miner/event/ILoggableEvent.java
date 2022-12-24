package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import org.jetbrains.annotations.NotNull;

public interface ILoggableEvent extends IEvent{
	@NotNull
	String getAsLog();
	
	@NotNull
	Webhook getAsWebhookEmbed();
	
	@NotNull
	Webhook getAsWebhookMessage();
}
