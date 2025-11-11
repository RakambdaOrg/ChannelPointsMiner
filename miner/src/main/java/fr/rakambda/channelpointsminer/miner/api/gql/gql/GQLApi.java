package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLError;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelfollows.ChannelFollowsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelfollows.ChannelFollowsOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus.ChatRoomBanStatusOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.claimcommunitypoints.ClaimCommunityPointsOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken.PlaybackAccessTokenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken.PlaybackAccessTokenOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.joinraid.JoinRaidData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.joinraid.JoinRaidOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.makeprediction.MakePredictionData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.makeprediction.MakePredictionOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.GetUserIdFromLoginData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.GetUserIdFromLoginOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden.SetDropsCommunityHighlightToHiddenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden.SetDropsCommunityHighlightToHiddenOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.FollowConnection;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.FollowEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.PageInfo;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.withislive.WithIsStreamLiveData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.withislive.WithIsStreamLiveOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@RequiredArgsConstructor
@Log4j2
public class GQLApi{
    private static final String ENDPOINT = "https://gql.twitch.tv/gql";
    private static final String CLIENT_INTEGRITY_HEADER = "Client-Integrity";
    private static final String CLIENT_ID_HEADER = "Client-ID";
    private static final String CLIENT_SESSION_ID_HEADER = "Client-Session-ID";
    private static final String CLIENT_VERSION_HEADER = "Client-Version";
    private static final String X_DEVICE_ID_HEADER = "X-Device-ID";
    
    private static final String ORDER_DESC = "DESC";
    private static final Set<String> EXPECTED_ERROR_MESSAGES = Set.of("service timeout", "service error", "server error", "service unavailable");
    private static final Set<String> INTEGRITY_ERROR_MESSAGES = Set.of("failed integrity check");
    
    private final TwitchLogin twitchLogin;
    private final UnirestInstance unirest;
    private final IIntegrityProvider integrityProvider;
    
    @NonNull
    public Optional<GQLResponse<GetUserIdFromLoginData>> getUserIdFromLogin(@NonNull String username){
        return postGqlRequest(new GetUserIdFromLoginOperation(username));
    }
    
    @NonNull
    private <T> Optional<GQLResponse<T>> postGqlRequest(@NonNull IGQLOperation<T> operation){
        try{
            log.debug("Sending GQL operation {}", operation);
            var integrity = integrityProvider.getIntegrity();
            
            var request = unirest.post(ENDPOINT)
                    .header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
                    .header(CLIENT_ID_HEADER, twitchLogin.getTwitchClient().getClientId());
            
            integrity.ifPresent(i -> request
                    .header(CLIENT_INTEGRITY_HEADER, i.getToken())
                    .header(CLIENT_SESSION_ID_HEADER, i.getClientSessionId())
                    .header(CLIENT_VERSION_HEADER, i.getClientVersion())
                    .header(X_DEVICE_ID_HEADER, i.getXDeviceId()));
            
            var response = request
                    .body(operation)
                    .asObject(operation.getResponseType());
            
            if(!response.isSuccess()){
                if(response.getStatus() == 401){
                    throw new InvalidCredentials(response.getStatus(), -1, "Invalid credentials provided");
                }
                return Optional.empty();
            }
            
            var body = response.getBody();
            if(body.isError()){
                var errors = body.getErrors();
                
                if(isErrorIntegrity(errors)){
                    log.error("Received GQL integrity error response: {}", errors);
                    integrityProvider.invalidate();
                }
                else if(isErrorExpected(errors)){
                    log.warn("Received GQL error response for {}: {}", operation.getOperationName(), errors);
                }
                else{
                    log.error("Received GQL error response for {}: {}", operation.getOperationName(), errors);
                }
                return Optional.empty();
            }
            
            return Optional.of(body);
        }
        catch(IntegrityException | InvalidCredentials e){
            throw new RuntimeException(e);
        }
        catch(Throwable e){
            log.error("Unknown error during GQL request", e);
            throw new RuntimeException(e);
        }
    }
    
    private boolean isErrorExpected(@NonNull Collection<GQLError> errors){
        return errors.stream().allMatch(this::isErrorExpected);
    }
    
    private boolean isErrorExpected(@NonNull GQLError error){
        return EXPECTED_ERROR_MESSAGES.contains(error.getMessage());
    }
    
    private boolean isErrorIntegrity(@NonNull Collection<GQLError> errors){
        return errors.stream().anyMatch(this::isErrorIntegrity);
    }
    
    private boolean isErrorIntegrity(@NonNull GQLError error){
        return INTEGRITY_ERROR_MESSAGES.contains(error.getMessage());
    }
    
    @NonNull
    public Optional<GQLResponse<ChannelPointsContextData>> channelPointsContext(@NonNull String username){
        return postGqlRequest(new ChannelPointsContextOperation(username));
    }
    
    @NonNull
    public Optional<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> videoPlayerStreamInfoOverlayChannel(@NonNull String username){
        return postGqlRequest(new VideoPlayerStreamInfoOverlayChannelOperation(username));
    }
    
    @NonNull
    public Optional<GQLResponse<DropsHighlightServiceAvailableDropsData>> dropsHighlightServiceAvailableDrops(@NonNull String channelId){
        return postGqlRequest(new DropsHighlightServiceAvailableDropsOperation(channelId));
    }
	
	@NonNull
	public Optional<GQLResponse<SetDropsCommunityHighlightToHiddenData>> setDropsCommunityHighlightToHidden(@NonNull String channelId, @NonNull String campaignId){
		return postGqlRequest(new SetDropsCommunityHighlightToHiddenOperation(channelId, campaignId));
	}
    
    @NonNull
    public Optional<GQLResponse<ClaimCommunityPointsData>> claimCommunityPoints(@NonNull String channelId, @NonNull String claimId){
        return postGqlRequest(new ClaimCommunityPointsOperation(channelId, claimId));
    }
    
    @NonNull
    public Optional<GQLResponse<CommunityMomentCalloutClaimData>> claimCommunityMoment(@NonNull String momentId){
        return postGqlRequest(new CommunityMomentCalloutClaimOperation(momentId));
    }
    
    @NonNull
    public Optional<GQLResponse<JoinRaidData>> joinRaid(@NonNull String raidId){
        return postGqlRequest(new JoinRaidOperation(raidId));
    }
    
    @NonNull
    public Optional<GQLResponse<InventoryData>> inventory(){
        return postGqlRequest(new InventoryOperation());
    }
    
    @NonNull
    public Optional<GQLResponse<DropsPageClaimDropRewardsData>> dropsPageClaimDropRewards(@NonNull String dropInstanceId){
        return postGqlRequest(new DropsPageClaimDropRewardsOperation(dropInstanceId));
    }
    
    @NonNull
    public Optional<GQLResponse<MakePredictionData>> makePrediction(@NonNull String eventId, @NonNull String outcomeId, int amount, @NonNull String transactionId){
        return postGqlRequest(new MakePredictionOperation(eventId, outcomeId, amount, transactionId));
    }
    
    @NonNull
    public Optional<GQLResponse<PlaybackAccessTokenData>> playbackAccessToken(@NonNull String login){
        return postGqlRequest(new PlaybackAccessTokenOperation(login));
    }
	
	@NonNull
	public Optional<GQLResponse<WithIsStreamLiveData>> withIsStreamLive(@NonNull String id){
		return postGqlRequest(new WithIsStreamLiveOperation(id));
	}
	
    @NonNull
    public List<User> allChannelFollows(){
        var follows = new ArrayList<User>();
        
        boolean hasNext;
        String cursor = null;
        do{
            var response = channelFollows(100, ORDER_DESC, cursor);
			if(response.isEmpty()){
				log.error("Failed to load follows, response is empty");
				break;
			}
			
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
                        .map(List::getLast)
                        .map(FollowEdge::getCursor)
                        .orElseThrow(() -> new IllegalStateException("Follows has next page but couldn't find cursor"));
            }
        }
        while(hasNext);
        
        return follows;
    }
	
    @NonNull
    public Optional<GQLResponse<ChannelFollowsData>> channelFollows(int limit, @NonNull String order, @Nullable String cursor){
        return postGqlRequest(new ChannelFollowsOperation(limit, order, cursor));
    }
	
    @NonNull
    public Optional<GQLResponse<ChatRoomBanStatusData>> chatRoomBanStatus(@NonNull String channelId, @NonNull String targetUserId){
        return postGqlRequest(new ChatRoomBanStatusOperation(channelId, targetUserId));
    }
}
