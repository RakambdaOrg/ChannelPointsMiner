package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.gql.data.request.ChannelPointsContextOperation;
import fr.raksrinana.twitchminer.api.gql.data.request.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.request.ReportMenuItemOperation;
import fr.raksrinana.twitchminer.api.gql.data.response.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.response.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.response.reportmenuitem.ReportMenuItemData;
import kong.unirest.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static kong.unirest.HeaderNames.AUTHORIZATION;

@Log4j2
public class GQLApi{
	private static final String ENDPOINT = "https://gql.twitch.tv/gql";
	
	@NotNull
	public static Optional<GQLResponse<ReportMenuItemData>> reportMenuItem(@NotNull String username){
		return postRequest(new ReportMenuItemOperation(username));
	}
	
	@NotNull
	private static <T> Optional<GQLResponse<T>> postRequest(@NotNull GQLOperation<T> operation){
		var response = Unirest.post(ENDPOINT)
				.header(AUTHORIZATION, "OAuth " + Main.getTwitchLogin().getAccessToken())
				.body(operation)
				.asObject(operation.getResponseType());
		
		if(!response.isSuccess()){
			Unirest.post(ENDPOINT)
					.header(AUTHORIZATION, "OAuth " + Main.getTwitchLogin().getAccessToken())
					.body(operation)
					.asString()
					.ifSuccess(r -> log.info(r.getBody()));
			return Optional.empty();
		}
		
		return Optional.ofNullable(response.getBody());
	}
	
	@NotNull
	public static Optional<GQLResponse<ChannelPointsContextData>> channelPointsContext(String username){
		return postRequest(new ChannelPointsContextOperation(username));
	}
}
