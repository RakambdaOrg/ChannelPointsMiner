package fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationDisplayType;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class CreateNotificationData{
	@JsonProperty("summary")
	@Nullable
	private NotificationSummary summary;
	@JsonProperty("notification")
	@NotNull
	private Notification notification;
	@JsonProperty("persistent")
	private boolean persistent;
	@JsonProperty("toast")
	private boolean toast;
	@JsonProperty("display_type")
	@NotNull
	private NotificationDisplayType displayType;
}
