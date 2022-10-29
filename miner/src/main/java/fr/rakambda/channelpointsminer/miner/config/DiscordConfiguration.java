package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Discord settings to send notifications.")
public class DiscordConfiguration{
	@JsonProperty(value = "webhookUrl", required = true)
	@JsonDeserialize(using = URLDeserializer.class)
	@JsonPropertyDescription("Discord webhook url to publish events to.")
	@Nullable
	private URL url;
	@JsonProperty("embeds")
	@JsonPropertyDescription("Use embeds in the messages or not. Default: false")
	@Builder.Default
	private boolean embeds = false;
}
