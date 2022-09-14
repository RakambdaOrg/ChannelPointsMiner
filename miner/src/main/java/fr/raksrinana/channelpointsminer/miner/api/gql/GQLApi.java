package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLError;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.IntegrityResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelfollows.ChannelFollowsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelfollows.ChannelFollowsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelpointscontext.ChannelPointsContextOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.chatroombanstatus.ChatRoomBanStatusOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.claimcommunitypoints.ClaimCommunityPointsOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimOperation;
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
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.IntegrityError;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import kong.unirest.core.Unirest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@Log4j2
public class GQLApi{
	public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String ENDPOINT = "https://gql.twitch.tv";
	private static final String CLIENT_INTEGRITY_HEADER = "Client-Integrity";
	private static final String CLIENT_ID_HEADER = "Client-ID";
	private static final String ORDER_DESC = "DESC";
	private static final Set<String> EXPECTED_ERROR_MESSAGES = Set.of("service timeout", "service error", "server error", "service unavailable");
	private static final String DEVICE_ID_HEADER = "Device-ID";
	private static final int DEVICE_ID_LENGTH = 26;
	
	private final TwitchLogin twitchLogin;
	private final String deviceId;
	private IntegrityResponse integrityResponse;
	
	public GQLApi(TwitchLogin twitchLogin){
		this.twitchLogin = twitchLogin;
		this.deviceId = RandomStringUtils.randomAlphanumeric(DEVICE_ID_LENGTH);
	}
	
	@NotNull
	public Optional<GQLResponse<ReportMenuItemData>> reportMenuItem(@NotNull String username){
		return postGqlRequest(new ReportMenuItemOperation(username));
	}
	
	@NotNull
	private <T> Optional<GQLResponse<T>> postGqlRequest(@NotNull IGQLOperation<T> operation){
		var response = Unirest.post(ENDPOINT + "/gql")
				.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
				.header(CLIENT_INTEGRITY_HEADER, getClientIntegrity())
				.header(CLIENT_ID_HEADER, CLIENT_ID)
				.header(DEVICE_ID_HEADER, deviceId)
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
			var errors = body.getErrors();
			
			if(isErrorExpected(errors)){
				log.warn("Received GQL error response: {}", errors);
			}
			else{
				log.error("Received GQL error response: {}", errors);
			}
			return Optional.empty();
		}
		
		return Optional.ofNullable(response.getBody());
	}
	
	@NotNull
	private String getClientIntegrity(){
		if(Objects.nonNull(integrityResponse) && integrityResponse.getExpiration().isAfter(TimeFactory.now())){
			return integrityResponse.getToken();
		}
		
		var response = Unirest.post(ENDPOINT + "/integrity")
				.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
				.asObject(IntegrityResponse.class);
		
		if(!response.isSuccess()){
			throw new RuntimeException(new IntegrityError(response.getStatus(), "Http code is not a success"));
		}
		
		var body = response.getBody();
		if(Objects.isNull(body.getToken())){
			throw new RuntimeException(new IntegrityError(response.getStatus(), body.getMessage()));
		}
		
		integrityResponse = body;
		return body.getToken();
	}
	
	private boolean isErrorExpected(@NotNull Collection<GQLError> errors){
		return errors.stream().allMatch(this::isErrorExpected);
	}
	
	private boolean isErrorExpected(@NotNull GQLError error){
		return EXPECTED_ERROR_MESSAGES.contains(error.getMessage());
	}
	
	@NotNull
	public Optional<GQLResponse<ChannelPointsContextData>> channelPointsContext(@NotNull String username){
		return postGqlRequest(new ChannelPointsContextOperation(username));
	}
	
	@NotNull
	public Optional<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> videoPlayerStreamInfoOverlayChannel(@NotNull String username){
		return postGqlRequest(new VideoPlayerStreamInfoOverlayChannelOperation(username));
	}
	
	@NotNull
	public Optional<GQLResponse<DropsHighlightServiceAvailableDropsData>> dropsHighlightServiceAvailableDrops(@NotNull String channelId){
		return postGqlRequest(new DropsHighlightServiceAvailableDropsOperation(channelId));
	}
	
	@NotNull
	public Optional<GQLResponse<ClaimCommunityPointsData>> claimCommunityPoints(@NotNull String channelId, @NotNull String claimId){
		return postGqlRequest(new ClaimCommunityPointsOperation(channelId, claimId));
	}
	
	@NotNull
	public Optional<GQLResponse<CommunityMomentCalloutClaimData>> claimCommunityMoment(@NotNull String momentId){
		return postGqlRequest(new CommunityMomentCalloutClaimOperation(momentId));
	}
	
	@NotNull
	public Optional<GQLResponse<JoinRaidData>> joinRaid(@NotNull String raidId){
		return postGqlRequest(new JoinRaidOperation(raidId));
	}
	
	@NotNull
	public Optional<GQLResponse<InventoryData>> inventory(){
		return postGqlRequest(new InventoryOperation());
	}
	
	@NotNull
	public Optional<GQLResponse<DropsPageClaimDropRewardsData>> dropsPageClaimDropRewards(@NotNull String dropInstanceId){
		return postGqlRequest(new DropsPageClaimDropRewardsOperation(dropInstanceId));
	}
	
	@NotNull
	public Optional<GQLResponse<MakePredictionData>> makePrediction(@NotNull String eventId, @NotNull String outcomeId, int amount, @NotNull String transactionId){
		return postGqlRequest(new MakePredictionOperation(eventId, outcomeId, amount, transactionId));
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
		return postGqlRequest(new ChannelFollowsOperation(limit, order, cursor));
	}
	
	@NotNull
	public Optional<GQLResponse<ChatRoomBanStatusData>> chatRoomBanStatus(@NotNull String channelId, @NotNull String targetUserId){
		return postGqlRequest(new ChatRoomBanStatusOperation(channelId, targetUserId));
	}
}
