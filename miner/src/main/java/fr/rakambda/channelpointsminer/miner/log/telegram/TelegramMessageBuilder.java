package fr.rakambda.channelpointsminer.miner.log.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class TelegramMessageBuilder {
	@NonNull
	public Message createSimpleMessage(@NonNull ILoggableEvent event, @Nullable MessageEventConfiguration config, @NonNull String chatId){
		var format = Optional.ofNullable(config).map(MessageEventConfiguration::getFormat).orElseGet(event::getDefaultFormat);
		return Message.builder()
				.text(formatMessage(event, format))
				.chatId(chatId)
				.build();
	}
	
	@NonNull
	private String formatMessage(@NonNull StringLookup event, @NonNull String format){
		var substitutor = new StringSubstitutor(event, "{", "}", '$');
		return substitutor.replace(format);
	}
}
