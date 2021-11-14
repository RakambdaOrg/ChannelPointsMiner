package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import java.util.Optional;

public interface ILogEvent{
	String getAsLog();
	
	Webhook getAsWebhookEmbed();
	
	Webhook getAsWebhookMessage();
	
	IMiner getMiner();
	
	Optional<Streamer> getStreamer();
}
