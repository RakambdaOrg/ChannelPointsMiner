package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import fr.rakambda.channelpointsminer.miner.log.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LogEventListenerFactory{
	@NotNull
	public static IEventHandler createLogger(){
		return new LoggerEventListener();
	}
	
	@NotNull
	public static IEventHandler createDiscordLogger(@NotNull DiscordApi discordApi, boolean useEmbeds){
		return new DiscordEventListener(discordApi, useEmbeds, e -> true);
	}
}