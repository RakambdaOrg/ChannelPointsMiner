package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.helix.HelixApi;
import fr.raksrinana.twitchminer.api.kraken.KrakenApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
}
