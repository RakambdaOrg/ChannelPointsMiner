package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.TelegramConfiguration;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordMessageBuilder;
import fr.rakambda.channelpointsminer.miner.log.telegram.TelegramEventListener;
import fr.rakambda.channelpointsminer.miner.log.telegram.TelegramMessageBuilder;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LogEventListenerFactory{
	@NonNull
	public static IEventHandler createLogger(){
		return new LoggerEventListener();
	}
	
	@NonNull
	public static IEventHandler createDiscordLogger(@NonNull DiscordApi discordApi, @NonNull DiscordConfiguration discordConfiguration){
		return new DiscordEventListener(discordApi, discordConfiguration, new DiscordMessageBuilder());
	}
	
	@NonNull
	public static IEventHandler createTelegramLogger(@NonNull TelegramApi telegramApi, @NonNull TelegramConfiguration telegramConfiguration){
		return new TelegramEventListener(telegramApi, telegramConfiguration, new TelegramMessageBuilder());
	}
}