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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Analytics settings define a way to collect data on your twitch account as time passes.")
public class AnalyticsConfiguration{
	@JsonProperty("enabled")
	@JsonPropertyDescription("Enable or disable data collection. Default: false")
	@Builder.Default
	private boolean enabled = false;
	@JsonProperty("database")
	@JsonPropertyDescription("Database settings.")
	@Nullable
	private DatabaseConfiguration database;
	@JsonProperty("recordUserPredictions")
	@JsonPropertyDescription("Record other chat members predictions. Default: false")
	@Builder.Default
	private boolean recordUserPredictions = false;
}
