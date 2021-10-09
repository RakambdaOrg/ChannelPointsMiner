package fr.raksrinana.twitchminer.miner.runnables;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.utils.CommonUtils;
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
		log.debug("Updating stream info");
		try{
			for(var streamer : miner.getStreamers()){
				update(streamer);
				CommonUtils.randomSleep(500, 100);
			}
			log.debug("Done updating stream info");
		}
		catch(Exception e){
			log.error("Failed to update stream info", e);
		}
	}
	
	public void update(@NotNull Streamer streamer){
		log.trace("Updating stream info for {}", streamer);
		var wasStreaming = streamer.isStreaming();
		
		miner.getGqlApi().videoPlayerStreamInfoOverlayChannel(streamer.getUsername())
				.map(GQLResponse::getData)
				.ifPresentOrElse(
						streamer::setVideoPlayerStreamInfoOverlayChannel,
						() -> streamer.setVideoPlayerStreamInfoOverlayChannel(null));
		
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
