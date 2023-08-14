package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClaimDropHandler extends PubSubMessageHandlerAdapter{
	@NotNull
	private final SyncInventory syncInventory;
	
	@Override
	public void onDropProgress(@NotNull Topic topic, @NotNull DropProgress message){
		if(message.getData().getCurrentProgressMin() >= message.getData().getRequiredProgressMin()){
			syncInventory.run();
		}
	}
}
