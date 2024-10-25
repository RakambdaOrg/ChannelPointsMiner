package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden.SetDropsCommunityHighlightToHiddenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.SetDropsCommunityHighlightToHiddenPayload;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
public class UpdateStreamInfo implements Runnable{
	private static final Collection<String> DISMISSIBLE_CAMPAIGNS = Set.of(
		"dc4ff0b4-4de0-11ef-9ec3-621fb0811846",
		"cbc3c726-8c0a-11ef-9b38-1ede7ff66562"
	);
	
	@NotNull
	private final IMiner miner;
	
	@Override
	public void run(){
		try(var ignored = LogContext.with(miner)){
			log.debug("Updating all stream info");
			try{
				miner.getStreamers().stream()
						.filter(Streamer::needUpdate)
						.forEach(streamer -> {
							run(streamer);
							CommonUtils.randomSleep(500, 100);
						});
				log.debug("Done updating all stream info");
			}
			catch(Exception e){
				log.error("Failed to update all stream info", e);
			}
		}
	}
	
	public void run(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			var wasStreaming = streamer.isStreaming();
			
			updateVideoInfo(streamer);
			updateSpadeUrl(streamer);
			updateM3u8Url(streamer);
			updateBanStatus(streamer);
			updatePointsContext(streamer);
			updateCampaigns(streamer);
			
			var now = TimeFactory.now();
			streamer.setLastUpdated(now);
			if(wasStreaming && !streamer.isStreaming()){
				if(streamer.hasStreamedEnoughTime()){
					streamer.setLastOffline(now);
				}
				streamer.resetWatchedDuration();
			}
		}
	}
	
	private void updateVideoInfo(@NotNull Streamer streamer){
		log.trace("Updating video info");
		
		miner.getGqlApi().videoPlayerStreamInfoOverlayChannel(streamer.getUsername())
				.map(GQLResponse::getData)
				.ifPresentOrElse(
						streamer::setVideoPlayerStreamInfoOverlayChannel,
						() -> streamer.setVideoPlayerStreamInfoOverlayChannel(null));
	}
	
	private void updateSpadeUrl(@NotNull Streamer streamer){
		log.trace("Updating spade url");
		if(streamer.isStreaming()){
			if(Objects.isNull(streamer.getSpadeUrl())){
				Optional.ofNullable(streamer.getChannelUrl())
						.flatMap(miner.getTwitchApi()::getSpadeUrl)
						.ifPresent(streamer::setSpadeUrl);
			}
		}
		else{
			streamer.setSpadeUrl(null);
		}
	}
	
	private void updateM3u8Url(@NotNull Streamer streamer){
		log.trace("Updating m3u8 url");
		if(streamer.isParticipateCampaigns() && streamer.isStreaming()){
			if(Objects.isNull(streamer.getM3u8Url())){
				var accessToken = miner.getGqlApi().playbackAccessToken(streamer.getUsername());
				if(accessToken.isEmpty()){
					log.warn("Failed to get playback access token for {}", streamer);
					return;
				}
				var token = accessToken.get().getData().getStreamPlaybackAccessToken();
				
				miner.getTwitchApi().getM3u8Url(streamer.getUsername(), token.getSignature(), token.getValue())
						.ifPresent(streamer::setM3u8Url);
			}
		}
		else{
			streamer.setM3u8Url(null);
		}
	}
	
	private void updateBanStatus(@NotNull Streamer streamer){
		log.trace("Updating ban status");
		if(streamer.isStreaming()){
			var banned = miner.getGqlApi().chatRoomBanStatus(streamer.getId(), miner.getTwitchLogin().fetchUserId(miner.getGqlApi()))
					.map(GQLResponse::getData)
					.map(ChatRoomBanStatusData::getChatRoomBanStatus)
					.isPresent();
			streamer.setChatBanned(banned);
		}
	}
	
	private void updatePointsContext(@NotNull Streamer streamer){
		log.trace("Updating channel points context");
		
		miner.getGqlApi().channelPointsContext(streamer.getUsername())
				.map(GQLResponse::getData)
				.ifPresentOrElse(
						streamer::setChannelPointsContext,
						() -> streamer.setChannelPointsContext(null));
		
		streamer.getClaimId().ifPresent(claimId -> miner.getGqlApi().claimCommunityPoints(streamer.getId(), claimId));
	}
	
	private void updateCampaigns(@NotNull Streamer streamer){
		log.trace("Updating campaigns");
		if(streamer.isParticipateCampaigns() && streamer.isStreaming() && streamer.isStreamingGame()){
			var dropsHighlightServiceAvailableDropsData = miner.getGqlApi().dropsHighlightServiceAvailableDrops(streamer.getId())
					.map(GQLResponse::getData);
			
			dropsHighlightServiceAvailableDropsData.ifPresentOrElse(
					streamer::setDropsHighlightServiceAvailableDrops,
					() -> streamer.setDropsHighlightServiceAvailableDrops(null));
			
			if(streamer.isDismissKnownGlobalCampaigns()){
				dropsHighlightServiceAvailableDropsData.stream()
						.map(DropsHighlightServiceAvailableDropsData::getChannel)
						.map(Channel::getViewerDropCampaigns)
						.flatMap(Collection::stream)
						.filter(dropCampaign -> DISMISSIBLE_CAMPAIGNS.contains(dropCampaign.getId()))
						.forEach(dropCampaign -> dismissCampaign(miner, streamer, dropCampaign));
			}
		}
		else{
			streamer.setDropsHighlightServiceAvailableDrops(null);
		}
	}
	
	private void dismissCampaign(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull DropCampaign dropCampaign){
		var result = miner.getGqlApi().setDropsCommunityHighlightToHidden(streamer.getId(), dropCampaign.getId());
		var isHidden = result
				.map(GQLResponse::getData)
				.map(SetDropsCommunityHighlightToHiddenData::getSetDropsCommunityHighlightToHiddenPayload)
				.map(SetDropsCommunityHighlightToHiddenPayload::isHidden)
				.orElse(false);
		
		log.info("Dismissed campaign {} on streamer {}, result {}", dropCampaign, streamer, isHidden);
		miner.updateStreamerInfos(streamer);
	}
}
