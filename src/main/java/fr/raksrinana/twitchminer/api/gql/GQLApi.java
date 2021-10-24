package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextOperation;
import fr.raksrinana.twitchminer.api.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.raksrinana.twitchminer.api.gql.data.claimcommunitypoints.ClaimCommunityPointsOperation;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsOperation;
import fr.raksrinana.twitchminer.api.gql.data.joinraid.JoinRaidData;
import fr.raksrinana.twitchminer.api.gql.data.joinraid.JoinRaidOperation;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemOperation;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelOperation;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.passport.exceptions.InvalidCredentials;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static kong.unirest.HeaderNames.AUTHORIZATION;

@Log4j2
@RequiredArgsConstructor
public class GQLApi{
	private static final String ENDPOINT = "https://gql.twitch.tv/gql";
	
	private final TwitchLogin twitchLogin;
	
	@NotNull
	public Optional<GQLResponse<ReportMenuItemData>> reportMenuItem(@NotNull String username){
		return postRequest(new ReportMenuItemOperation(username));
	}
	
	@NotNull
	public Optional<GQLResponse<ChannelPointsContextData>> channelPointsContext(@NotNull String username){
		return postRequest(new ChannelPointsContextOperation(username));
	}
	
	@NotNull
	public Optional<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> videoPlayerStreamInfoOverlayChannel(@NotNull String username){
		return postRequest(new VideoPlayerStreamInfoOverlayChannelOperation(username));
	}
	
	@NotNull
	public Optional<GQLResponse<DropsHighlightServiceAvailableDropsData>> dropsHighlightServiceAvailableDrops(@NotNull String channelId){
		return postRequest(new DropsHighlightServiceAvailableDropsOperation(channelId));
	}
	
	@NotNull
	public Optional<GQLResponse<ClaimCommunityPointsData>> claimCommunityPoints(@NotNull String channelId, @NotNull String claimId){
		return postRequest(new ClaimCommunityPointsOperation(channelId, claimId));
	}
	
	@NotNull
	public Optional<GQLResponse<JoinRaidData>> joinRaid(@NotNull String raidId){
		return postRequest(new JoinRaidOperation(raidId));
	}
	
	@NotNull
	private <T> Optional<GQLResponse<T>> postRequest(@NotNull GQLOperation<T> operation){
		var response = Unirest.post(ENDPOINT)
				.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
				.body(operation)
				.asObject(operation.getResponseType());
		
		if(!response.isSuccess()){
			if(response.getStatus() == 401){
				throw new RuntimeException(new InvalidCredentials(response.getStatus(), -1, "Invalid credentials provided"));
			}
			return Optional.empty();
		}
		
		var body = response.getBody();
		if(body.isError()){
			log.error("Received GQL error response: {}", body);
			return Optional.empty();
		}
		
		return Optional.ofNullable(response.getBody());
	}
}
