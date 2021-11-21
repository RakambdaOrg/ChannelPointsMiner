package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.log.DiscordLogEventListener;
import fr.raksrinana.channelpointsminer.log.ILogEventListener;
import fr.raksrinana.channelpointsminer.log.LoggerLogEventListener;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LogEventListenerFactory{
	@NotNull
	public static ILogEventListener createLogger(){
		return new LoggerLogEventListener();
	}
	
	@NotNull
	public static ILogEventListener createDiscordLogger(@NotNull DiscordApi discordApi, boolean useEmbeds){
		return new DiscordLogEventListener(discordApi, useEmbeds);
	}
}