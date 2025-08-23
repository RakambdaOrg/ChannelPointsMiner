package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.NotificationData;
import lombok.*;
import lombok.experimental.SuperBuilder;

@JsonTypeName("notification")
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class NotificationResponse extends ITwitchHermesWebSocketResponse{
	@JsonProperty("notification")
	private NotificationData notification;
}
