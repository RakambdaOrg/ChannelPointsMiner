package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.helix.HelixApi;
import fr.raksrinana.twitchminer.api.kraken.KrakenApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import org.jetbrains.annotations.NotNull;

public class ApiFactory{
	@NotNull
	public static GQLApi getGqlApi(@NotNull TwitchLogin twitchLogin){
		return new GQLApi(twitchLogin);
	}
	
	@NotNull
	public static HelixApi getHelixApi(@NotNull TwitchLogin twitchLogin){
		return new HelixApi(twitchLogin);
	}
	
	@NotNull
	public static KrakenApi getKrakenApi(@NotNull TwitchLogin twitchLogin){
		return new KrakenApi(twitchLogin);
	}
	
	@NotNull
	public static TwitchApi getTwitchApi(){
		return new TwitchApi();
	}
}
