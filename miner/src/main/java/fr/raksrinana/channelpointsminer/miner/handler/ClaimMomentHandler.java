package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimedMomentEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

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
					.filter(moment -> Objects.isNull(moment.getError()))
					.map(moment -> new ClaimedMomentEvent(miner, channelId, username, streamer, TimeFactory.now()))
					.ifPresent(miner::onEvent);
		}
	}
}
