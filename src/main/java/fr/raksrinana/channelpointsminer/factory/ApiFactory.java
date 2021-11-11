package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.helix.HelixApi;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ApiFactory{
	@NotNull
	public static GQLApi createGqlApi(@NotNull TwitchLogin twitchLogin){
		return new GQLApi(twitchLogin);
	}
	
	@NotNull
	public static HelixApi createHelixApi(@NotNull TwitchLogin twitchLogin){
		return new HelixApi(twitchLogin);
	}
	
	@NotNull
	public static KrakenApi createKrakenApi(@NotNull TwitchLogin twitchLogin){
		return new KrakenApi(twitchLogin);
	}
	
	@NotNull
	public static TwitchApi createTwitchApi(){
		return new TwitchApi();
	}
	
	@NotNull
	public static DiscordApi createdDiscordApi(@NotNull URL webhookUrl){
		return new DiscordApi(webhookUrl);
	}
}
