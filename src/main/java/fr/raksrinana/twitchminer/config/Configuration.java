package fr.raksrinana.twitchminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class Configuration{
	@NotNull
	@JsonProperty("username")
	@Comment(value = "Username of your Twitch account.")
	private String username;
	@NotNull
	@JsonProperty("password")
	@Comment(value = "Password of your Twitch account.")
	private String password;
	@JsonProperty("use2FA")
	@Comment(value = "If this account uses 2FA set this to true to directly ask for it.", defaultValue = "false")
	@Builder.Default
	private boolean use2Fa = false;
	@NotNull
	@JsonProperty("authenticationFolder")
	@Comment(value = "Path to a folder that contains authentication used to log back in after a restart.", defaultValue = "./authentication")
	@Builder.Default
	private Path authenticationFolder = Paths.get("authentication");
	@JsonProperty("loadFollows")
	@Comment(value = "Load streamers to scrape from follow list.", defaultValue = "false")
	@Builder.Default
	private boolean loadFollows = false;
	@JsonProperty("defaultStreamerSettings")
	@Comment(value = "Default settings for the streamers mined")
	@Builder.Default
	private StreamerSettings defaultStreamerSettings = new StreamerSettings();
	@JsonProperty("streamers")
	@Comment("List of streamers to scrape")
	@Builder.Default
	private Set<StreamerConfiguration> streamers = new HashSet<>();
}
