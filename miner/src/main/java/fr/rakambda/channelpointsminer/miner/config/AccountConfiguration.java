package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import fr.rakambda.channelpointsminer.miner.config.login.ILoginMethod;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Mining account configuration.")
public class AccountConfiguration{
	@JsonProperty("enabled")
	@JsonPropertyDescription("If the account is marked as enabled it'll be mined. Default: true")
	@Builder.Default
	private boolean enabled = true;
	@NonNull
	@JsonProperty(value = "username", required = true)
	@JsonPropertyDescription("Mining account's username.")
	private String username;
	@NonNull
	@JsonProperty(value = "loginMethod", required = true)
	@JsonPropertyDescription("Login method to use.")
	private ILoginMethod loginMethod;
	@JsonProperty("loadFollows")
	@JsonPropertyDescription("Load streamers to scrape from follow list. Default: false")
	@Builder.Default
	private boolean loadFollows = false;
	@JsonProperty("defaultStreamerSettings")
	@JsonPropertyDescription("Default streamer settings.")
	@Builder.Default
	private StreamerSettings defaultStreamerSettings = new StreamerSettings();
	@NonNull
	@JsonProperty("streamerConfigDirectories")
	@JsonPropertyDescription("Paths containing overrides for streamer configurations.")
	@Builder.Default
	private List<StreamerDirectory> streamerConfigDirectories = new ArrayList<>();
	@NonNull
	@JsonProperty("discord")
	@JsonPropertyDescription("Discord settings to send notifications.")
	@Builder.Default
	private DiscordConfiguration discord = new DiscordConfiguration();
	@NonNull
	@JsonProperty("telegram")
	@JsonPropertyDescription("Telegram settings to send notifications.")
	@Builder.Default
	private TelegramConfiguration telegram = new TelegramConfiguration();
	@JsonProperty("reloadEvery")
	@JsonPropertyDescription("Reload streamer settings every x minutes. Zero or negative value disables it. Default: 0")
	@Builder.Default
	private int reloadEvery = 0;
	@JsonProperty("analytics")
	@NonNull
	@JsonPropertyDescription("Analytics settings, recording account's evolution, bets, predictions.")
	@Builder.Default
	private AnalyticsConfiguration analytics = new AnalyticsConfiguration();
	@JsonProperty("chatMode")
	@NonNull
	@JsonPropertyDescription("Method used to join chat. Default: WS")
	@Builder.Default
	private ChatMode chatMode = ChatMode.WS;
	@JsonProperty("versionProvider")
	@NonNull
	@JsonPropertyDescription("Method used to get twitch version. Default: WEBPAGE")
	@Builder.Default
	private VersionProvider versionProvider = VersionProvider.WEBPAGE;
}
