package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.nio.file.Path;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ApiFactory{
	@NotNull
	public static GQLApi createGqlApi(@NotNull TwitchLogin twitchLogin){
		var clientSessionId = CommonUtils.randomHex(16);
		var xDeviceId = CommonUtils.randomAlphanumeric(32);
		return new GQLApi(twitchLogin, clientSessionId, xDeviceId);
	}
	
	@NotNull
	public static TwitchApi createTwitchApi(){
		return new TwitchApi();
	}
	
	@NotNull
	public static DiscordApi createdDiscordApi(@NotNull URL webhookUrl){
		return new DiscordApi(webhookUrl);
	}
	
	@NotNull
	public static PassportApi createPassportApi(@NotNull String username, @NotNull String password, @NotNull Path authenticationFolder, boolean use2Fa){
		return new PassportApi(username, password, authenticationFolder, use2Fa);
	}
}
