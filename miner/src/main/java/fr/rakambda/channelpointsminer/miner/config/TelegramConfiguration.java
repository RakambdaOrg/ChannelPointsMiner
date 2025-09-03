package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Telegram settings to send notifications.")
public class TelegramConfiguration {
	@JsonProperty(value = "token", required = true)
	@JsonPropertyDescription("Telegram bot token, can be obtained from 'BotFather'")
	@Nullable
	private String token;
	@JsonProperty(value = "chatId", required = true)
	@JsonPropertyDescription("Chat ID to send messages to, can be obtained from @getmyid_bot")
	@Nullable
	private String chatId;
	@JsonProperty("events")
	@JsonPropertyDescription("Customize events that are sent. Key is the name of an event (can be seen in the event/impl package). Default: all events with default format")
	@Builder.Default
	private Map<String, MessageEventConfiguration> events = new HashMap<>();
}
