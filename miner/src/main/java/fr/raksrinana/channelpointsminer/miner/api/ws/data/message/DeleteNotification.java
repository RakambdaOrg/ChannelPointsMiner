package fr.raksrinana.channelpointsminer.miner.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.deletenotification.DeleteNotificationData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("delete-notification")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteNotification extends IPubSubMessage{
	@JsonProperty("data")
	@NotNull
	private DeleteNotificationData data;
}
