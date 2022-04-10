package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelfollows.ChannelFollowsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelfollows.ChannelFollowsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelpointscontext.ChannelPointsContextOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.claimcommunitypoints.ClaimCommunityPointsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.inventory.InventoryData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.inventory.InventoryOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.joinraid.JoinRaidData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.joinraid.JoinRaidOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.makeprediction.MakePredictionOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.reportmenuitem.ReportMenuItemOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.FollowConnection;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.FollowEdge;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.PageInfo;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelOperation;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import kong.unirest.core.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@Log4j2
@RequiredArgsConstructor
public class GQLApi{
	private static final String ENDPOINT = "https://gql.twitch.tv/gql";
	private static final String ORDER_DESC = "DESC";
	
	private final TwitchLogin twitchLogin;
	
	@NotNull
	public Optional<GQLResponse<ReportMenuItemData>> reportMenuItem(@NotNull String username){
		return postRequest(new ReportMenuItemOperation(username));
	}
	
	@NotNull
	private <T> Optional<GQLResponse<T>> postRequest(@NotNull IGQLOperation<T> operation){
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
	public Optional<GQLResponse<InventoryData>> inventory(){
		return postRequest(new InventoryOperation());
	}
	
	@NotNull
	public Optional<GQLResponse<DropsPageClaimDropRewardsData>> dropsPageClaimDropRewards(@NotNull String dropInstanceId){
		return postRequest(new DropsPageClaimDropRewardsOperation(dropInstanceId));
	}
	
	@NotNull
	public Optional<GQLResponse<MakePredictionData>> makePrediction(@NotNull String eventId, @NotNull String outcomeId, int amount, @NotNull String transactionId){
		return postRequest(new MakePredictionOperation(eventId, outcomeId, amount, transactionId));
	}
	
	@NotNull
	public List<User> allChannelFollows(){
		var follows = new ArrayList<User>();
		
		boolean hasNext;
		String cursor = null;
		do{
			var response = channelFollows(100, ORDER_DESC, cursor);
			var followConnection = response.map(GQLResponse::getData).map(ChannelFollowsData::getUser).map(User::getFollows);
			
			followConnection.stream()
					.map(FollowConnection::getEdges)
					.flatMap(Collection::stream)
					.map(FollowEdge::getNode)
					.forEach(follows::add);
			
			hasNext = followConnection.map(FollowConnection::getPageInfo).map(PageInfo::isHasNextPage).orElse(false);
			if(hasNext){
				cursor = followConnection.map(FollowConnection::getEdges)
						.filter(followEdges -> !followEdges.isEmpty())
						.map(edge -> edge.get(edge.size() - 1))
						.map(FollowEdge::getCursor)
						.orElseThrow(() -> new IllegalStateException("Follows has next page but couldn't find cursor"));
			}
		}
		while(hasNext);
		
		return follows;
	}
	
	@NotNull
	public Optional<GQLResponse<ChannelFollowsData>> channelFollows(int limit, @NotNull String order, @Nullable String cursor){
		return postRequest(new ChannelFollowsOperation(limit, order, cursor));
	}
}
