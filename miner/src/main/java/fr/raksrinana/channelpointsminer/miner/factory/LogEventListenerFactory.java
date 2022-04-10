package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.log.DiscordEventListener;
import fr.raksrinana.channelpointsminer.miner.log.LoggerEventListener;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LogEventListenerFactory{
	@NotNull
	public static IEventListener createLogger(){
		return new LoggerEventListener();
	}
	
	@NotNull
	public static IEventListener createDiscordLogger(@NotNull DiscordApi discordApi, boolean useEmbeds){
		return new DiscordEventListener(discordApi, useEmbeds);
	}
}