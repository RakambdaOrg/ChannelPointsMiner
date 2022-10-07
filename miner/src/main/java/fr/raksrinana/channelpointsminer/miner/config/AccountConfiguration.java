package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.config.login.ILoginMethod;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountConfiguration{
	@JsonProperty("enabled")
	@Comment(value = "If the account is marked as enabled it'll be mined.", defaultValue = "true")
	@Builder.Default
	private boolean enabled = true;
	@NotNull
	@JsonProperty("username")
	@Comment(value = "Username of your Twitch account.")
	private String username;
	@NotNull
	@JsonProperty("loginMethod")
	private ILoginMethod loginMethod;
	@JsonProperty("loadFollows")
	@Comment(value = "Load streamers to scrape from follow list.", defaultValue = "false")
	@Builder.Default
	private boolean loadFollows = false;
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
	@JsonProperty("chatMode")
	@NotNull
	@Comment(value = "Method used to join chat")
	@Builder.Default
	private ChatMode chatMode = ChatMode.WS;
	@JsonProperty("versionProvider")
	@NotNull
	@Comment(value = "Method used to get twitch version")
	@Builder.Default
	private VersionProvider versionProvider = VersionProvider.WEBPAGE;
}
