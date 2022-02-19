package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsConfiguration{
	@JsonProperty("enabled")
	@Comment(value = "Enable or disable data collection")
	@Builder.Default
	private boolean enabled = false;
	@JsonProperty("database")
	@Comment(value = "Database settings")
	@Nullable
	private DatabaseConfiguration database;
}
