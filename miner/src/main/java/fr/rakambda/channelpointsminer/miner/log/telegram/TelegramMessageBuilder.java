package fr.rakambda.channelpointsminer.miner.log.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TelegramMessageBuilder {
	@NotNull
	public Message createSimpleMessage(@NotNull ILoggableEvent event, @Nullable MessageEventConfiguration config, @NotNull String chatId){
		var format = Optional.ofNullable(config).map(MessageEventConfiguration::getFormat).orElseGet(event::getDefaultFormat);
		return Message.builder()
				.text(formatMessage(event, format))
				.chatId(chatId)
				.build();
	}
	
	@NotNull
	private String formatMessage(@NotNull StringLookup event, @NotNull String format){
		var substitutor = new StringSubstitutor(event, "{", "}", '$');
		return substitutor.replace(format);
	}
}
