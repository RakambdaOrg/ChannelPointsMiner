package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountConfiguration{
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
	@JsonProperty("enabled")
	@Comment(value = "If the account is marked as enabled it'll be mined.", defaultValue = "true")
	@Builder.Default
	private boolean enabled = true;
	@JsonProperty("defaultStreamerSettings")
	@Comment(value = "Default settings for the streamers mined.")
	@Builder.Default
	private StreamerSettings defaultStreamerSettings = new StreamerSettings();
	@NotNull
	@JsonProperty("streamerConfigDirectories")
	@Comment(value = "List of paths to a folder that'll contain streamer configurations.", defaultValue = "<empty>")
	@Builder.Default
	private List<StreamerDirectory> streamerConfigDirectories = new ArrayList<>();
	@NotNull
	@JsonProperty("discord")
	@Comment(value = "Discord settings")
	@Builder.Default
	private DiscordConfiguration discord = new DiscordConfiguration();
	@JsonProperty("reloadEvery")
	@Comment(value = "Reload streamer settings every x minutes.", defaultValue = "0")
	@Builder.Default
	private int reloadEvery = 0;
	@JsonProperty("analytics")
	@NotNull
	@Comment(value = "Analytics settings")
	@Builder.Default
	private AnalyticsConfiguration analytics = new AnalyticsConfiguration();
}
