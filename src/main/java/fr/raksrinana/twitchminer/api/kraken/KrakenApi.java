package fr.raksrinana.twitchminer.api.kraken;

import fr.raksrinana.twitchminer.api.kraken.data.follows.Follow;
import fr.raksrinana.twitchminer.api.kraken.data.follows.GetFollowsResponse;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.AUTHORIZATION;

/**
 * @deprecated Will be shut down on February, 28 2022, see https://blog.twitch.tv/en/2021/07/15/legacy-twitch-api-v5-shutdown-details-and-timeline
 */
@Log4j2
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class KrakenApi{
	private static final String ENDPOINT = "https://api.twitch.tv/kraken";
	private static final String CLIENT_ID = "jzkbprff40iqj646a697cyrvl0zt2m6";
	
	private final TwitchLogin twitchLogin;
	
	@NotNull
	public List<Follow> getFollows(){
		return getFollows(100, 0);
	}
	
	@NotNull
	public List<Follow> getFollows(int limit, int offset){
		var response = Unirest.get(ENDPOINT + "/users/{userId}/follows/channels")
				.header(AUTHORIZATION, "Bearer " + twitchLogin.getAccessToken())
				.header(ACCEPT, "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.routeParam("userId", twitchLogin.fetchUserId())
				.queryString("limit", limit)
				.queryString("offset", offset)
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.asObject(GetFollowsResponse.class);
		
		if(!response.isSuccess()){
			return List.of();
		}
		
		var follows = new ArrayList<>(response.getBody().getFollows());
		if(follows.size() >= limit){
			follows.addAll(getFollows(limit, offset + limit));
		}
		return follows;
	}
}
