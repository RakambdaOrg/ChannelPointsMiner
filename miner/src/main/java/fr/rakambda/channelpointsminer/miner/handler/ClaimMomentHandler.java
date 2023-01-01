package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimedMomentEvent;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class ClaimMomentHandler extends PubSubMessageHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onCommunityMomentStart(@NotNull Topic topic, @NotNull CommunityMomentStart message){
		var channelId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(channelId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			miner.onEvent(new ClaimMomentEvent(miner, channelId, username, streamer, TimeFactory.now()));
			miner.getGqlApi().claimCommunityMoment(message.getData().getMomentId())
					.filter(response -> !response.isError())
					.map(GQLResponse::getData)
					.map(CommunityMomentCalloutClaimData::getMoment)
					.filter(moment -> {
						if(Objects.isNull(moment.getError())){
							return true;
						}
						log.error("Failed to claim moment due to `{}`", moment.getError());
						return false;
					})
					.map(moment -> new ClaimedMomentEvent(miner, channelId, username, streamer, TimeFactory.now()))
					.ifPresent(miner::onEvent);
		}
	}
}
