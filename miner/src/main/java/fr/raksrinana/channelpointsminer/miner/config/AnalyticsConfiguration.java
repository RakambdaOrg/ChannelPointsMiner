package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnalyticsConfiguration{
	@JsonProperty("enabled")
	@Comment(value = "Enable or disable data collection")
	@Builder.Default
	private boolean enabled = false;
	@JsonProperty("database")
	@Comment(value = "Database settings")
	@Nullable
	private DatabaseConfiguration database;
    @JsonProperty("recordChatsPredictions")
    @Comment(value = "Record other chat members predictions.")
    @Builder.Default
    private boolean recordChatsPredictions = false;
}
