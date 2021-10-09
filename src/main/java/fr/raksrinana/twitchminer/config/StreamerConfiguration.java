package fr.raksrinana.twitchminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StreamerConfiguration{
	@JsonProperty("username")
	@Comment("Username of the streamer")
	@NotNull
	private String username;
	@JsonProperty("settings")
	@Comment("Custom settings for this streamer (values defined here overrides the default config)")
	@Nullable
	private StreamerSettings settings;
	
	@EqualsAndHashCode.Include
	private String normalizedUsername() {
		return username.toLowerCase();
	}
}
