package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextOperation;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsOperation;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemOperation;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelOperation;
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
	
	public static Optional<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> videoPlayerStreamInfoOverlayChannel(String username){
		return postRequest(new VideoPlayerStreamInfoOverlayChannelOperation(username));
	}
	
	public static Optional<GQLResponse<DropsHighlightServiceAvailableDropsData>> dropsHighlightServiceAvailableDrops(String id){
		return postRequest(new DropsHighlightServiceAvailableDropsOperation(id));
	}
}
