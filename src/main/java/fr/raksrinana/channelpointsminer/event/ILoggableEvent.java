package fr.raksrinana.channelpointsminer.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import org.jetbrains.annotations.NotNull;

public interface ILoggableEvent extends IEvent{
	@NotNull
	String getAsLog();
	
	@NotNull
	Webhook getAsWebhookEmbed();
	
	@NotNull
	Webhook getAsWebhookMessage();
}
