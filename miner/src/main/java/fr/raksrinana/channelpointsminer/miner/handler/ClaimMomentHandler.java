package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClaimMomentHandler extends PubSubMessageHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onCommunityMomentStart(@NotNull Topic topic, @NotNull CommunityMomentStart message){
		try(var ignored = LogContext.with(miner)){
			miner.onEvent(new ClaimMomentEvent(miner, "FAKE-CHANNEL_ID", "FAKE-USERNAME", null, TimeFactory.now()));
			miner.getGqlApi().claimCommunityMoment(message.getData().getMomentId());
		}
	}
}
