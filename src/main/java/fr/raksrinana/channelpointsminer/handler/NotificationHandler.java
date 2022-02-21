package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.CreateNotification;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@RequiredArgsConstructor
public class NotificationHandler extends HandlerAdapter{
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
