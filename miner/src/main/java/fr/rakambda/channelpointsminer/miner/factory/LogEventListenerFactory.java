package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordMessageBuilder;
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
	public static IEventHandler createDiscordLogger(@NotNull DiscordApi discordApi, @NotNull DiscordConfiguration discordConfiguration){
		return new DiscordEventListener(discordApi, discordConfiguration, new DiscordMessageBuilder());
	}
}