package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface ILogEvent{
	@NotNull
	String getAsLog();
	
	@NotNull
	Webhook getAsWebhookEmbed();
	
	@NotNull
	Webhook getAsWebhookMessage();
	
	@NotNull
	IMiner getMiner();
	
	@NotNull
	Optional<Streamer> getStreamer();
}
