package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Customization of the event sent.")
public class DiscordEventConfiguration{
	@JsonProperty("format")
	@JsonPropertyDescription(value = "Format of the message sent. Will be either the text, or the description of the embed. Placeholders are between braces {example_key} . Keys can be seen in EventVariableKey class.")
	@Nullable
	private String format;
}
