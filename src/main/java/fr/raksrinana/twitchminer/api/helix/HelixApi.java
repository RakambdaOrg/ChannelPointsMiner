package fr.raksrinana.twitchminer.api.helix;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.helix.data.follows.Follow;
import fr.raksrinana.twitchminer.api.helix.data.follows.GetFollowsResponse;
import kong.unirest.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static kong.unirest.HeaderNames.AUTHORIZATION;

@Log4j2
public class HelixApi{
	private static final String ENDPOINT = "https://api.twitch.tv/helix";
	private static final String CLIENT_ID = "jzkbprff40iqj646a697cyrvl0zt2m6";
	
	@NotNull
	public static List<Follow> getFollows(){
		return getFollows(100, null);
	}
	
	@NotNull
	public static List<Follow> getFollows(int limit, String page){
		var query = Unirest.get(ENDPOINT + "/users/follows")
				.header(AUTHORIZATION, "Bearer " + Main.getTwitchLogin().getAccessToken())
				.header("Client-Id", CLIENT_ID)
				.queryString("fromId", Main.getTwitchLogin().getUserId())
				.queryString("first", limit);
		
		if(Objects.nonNull(page)){
			query = query.queryString("after", page);
		}
		
		var response = query.asObject(GetFollowsResponse.class);
		
		if(!response.isSuccess()){
			log.error("Failed to get follows, statusCode={}", response.getStatus());
			return List.of();
		}
		
		var body = response.getBody();
		var follows = new ArrayList<>(body.getFollows());
		var pagination = body.getPagination();
		if(Objects.nonNull(pagination) && Objects.nonNull(pagination.getCursor())){
			follows.addAll(getFollows(limit, pagination.getCursor()));
		}
		return follows;
	}
}
