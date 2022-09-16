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
import kong.unirest.core.UnirestInstance;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@Log4j2
public class GQLApi{
	private static final String ENDPOINT = "https://gql.twitch.tv";
	private static final String CLIENT_INTEGRITY_HEADER = "Client-Integrity";
	private static final String CLIENT_ID_HEADER = "Client-ID";
	private static final String CLIENT_SESSION_ID_HEADER = "Client-Session-ID";
	private static final String CLIENT_VERSION_HEADER = "Client-Version";
	private static final String X_DEVICE_ID_HEADER = "X-Device-ID";
	
	private static final String ORDER_DESC = "DESC";
	private static final Set<String> EXPECTED_ERROR_MESSAGES = Set.of("service timeout", "service error", "server error", "service unavailable");
	
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	
	private static final Pattern TWILIGHT_BUILD_ID_PATTERN = Pattern.compile("window\\.__twilightBuildID=\"([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12})\";");
	
	private final TwitchLogin twitchLogin;
	private final UnirestInstance unirest;
	private final String clientSessionId;
	private final String xDeviceId;
	
	private IntegrityResponse integrityResponse;
	private String clientVersion;
	
	public GQLApi(@NotNull TwitchLogin twitchLogin, @NotNull UnirestInstance unirest, @NotNull String clientSessionId, @NotNull String xDeviceId, @NotNull String defaultClientVersion){
		this.twitchLogin = twitchLogin;
		this.unirest = unirest;
		this.clientSessionId = clientSessionId;
		this.xDeviceId = xDeviceId;
		this.clientVersion = defaultClientVersion;
	}
	
	@NotNull
	public Optional<GQLResponse<ReportMenuItemData>> reportMenuItem(@NotNull String username){
		return postGqlRequest(new ReportMenuItemOperation(username));
	}
	
	@NotNull
	private <T> Optional<GQLResponse<T>> postGqlRequest(@NotNull IGQLOperation<T> operation){
		var clientIntegrity = getClientIntegrity();
		var response = unirest.post(ENDPOINT + "/gql")
				.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
				.header(CLIENT_INTEGRITY_HEADER, clientIntegrity)
				.header(CLIENT_ID_HEADER, CLIENT_ID)
				.header(CLIENT_SESSION_ID_HEADER, clientSessionId)
				.header(CLIENT_VERSION_HEADER, clientVersion)
				.header(X_DEVICE_ID_HEADER, xDeviceId)
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
		synchronized(this){
			if(Objects.nonNull(integrityResponse) && integrityResponse.getExpiration().isAfter(TimeFactory.now())){
				return integrityResponse.getToken();
			}
			
			updateClientVersion();
			
			log.info("Querying new integrity token");
			var response = unirest.post(ENDPOINT + "/integrity")
					.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
					.header(CLIENT_ID_HEADER, CLIENT_ID)
					.header(CLIENT_SESSION_ID_HEADER, clientSessionId)
					.header(CLIENT_VERSION_HEADER, clientVersion)
					.header(X_DEVICE_ID_HEADER, xDeviceId)
					.asObject(IntegrityResponse.class);
			
			if(!response.isSuccess()){
				throw new RuntimeException(new IntegrityError(response.getStatus(), "Http code is not a success"));
			}
			
			var body = response.getBody();
			if(Objects.isNull(body.getToken())){
				throw new RuntimeException(new IntegrityError(response.getStatus(), body.getMessage()));
			}
			
			log.info("New integrity token will expire at {}", body.getExpiration());
			integrityResponse = body;
			return body.getToken();
		}
	}
	
	private void updateClientVersion(){
		log.info("Querying new client version");
		var response = unirest.get("https://www.twitch.tv").asString();
		if(!response.isSuccess()){
			log.warn("Failed to update client version, status is : " + response.getStatus());
			return;
		}
		
		var page = response.getBody();
		if(Objects.isNull(page)){
			log.warn("Failed to update client version, null page");
			return;
		}
		
		var matcher = TWILIGHT_BUILD_ID_PATTERN.matcher(page);
		if(!matcher.find()){
			log.warn("Failed to update client version, didn't find version in page");
			return;
		}
		
		clientVersion = matcher.group(1);
		log.info("Current client version is {}", clientVersion);
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
