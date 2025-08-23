package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.NotificationData;
import lombok.*;

@JsonTypeName("notification")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationResponse extends ITwitchHermesWebSocketResponse{
	@JsonProperty("notification")
	private NotificationData notification;
}
