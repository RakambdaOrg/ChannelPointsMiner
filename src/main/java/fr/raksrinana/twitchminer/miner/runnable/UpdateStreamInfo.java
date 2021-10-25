package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.log.LogContext;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import fr.raksrinana.twitchminer.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class UpdateStreamInfo implements Runnable{
	@NotNull
	private final IMiner miner;
	
	@Override
	public void run(){
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
	
	public void run(@NotNull Streamer streamer){
		try(var ignored = LogContext.with(streamer)){
			var wasStreaming = streamer.isStreaming();
			
			updateVideoInfo(streamer);
			updateSpadeUrl(streamer);
			updatePointsContext(streamer);
			updateCampaigns(streamer);
			
			var now = TimeFactory.now();
			streamer.setLastUpdated(now);
			if(wasStreaming && !streamer.isStreaming()){
				streamer.setLastOffline(now);
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
		if(streamer.updateCampaigns() && streamer.isStreaming() && streamer.isStreamingGame()){
			miner.getGqlApi().dropsHighlightServiceAvailableDrops(streamer.getId())
					.map(GQLResponse::getData)
					.ifPresentOrElse(
							streamer::setDropsHighlightServiceAvailableDrops,
							() -> streamer.setDropsHighlightServiceAvailableDrops(null));
		}
		else{
			streamer.setDropsHighlightServiceAvailableDrops(null);
		}
	}
}
