package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CreateNotification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@RequiredArgsConstructor
public class NotificationHandler extends PubSubMessageHandlerAdapter{
	public static final String DROP_REWARD_REMINDER = "user_drop_reward_reminder_notification";
	
	private final IMiner miner;
	
	@Override
	public void onCreateNotification(@NotNull Topic topic, @NotNull CreateNotification message){
		try(var ignored = LogContext.with(miner)){
			var type = message.getData().getNotification().getType();
			if(Objects.equals(type, DROP_REWARD_REMINDER)){
				miner.syncInventory();
			}
		}
	}
}
