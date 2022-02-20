package fr.raksrinana.channelpointsminer.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class CreateNotificationData{
	@JsonProperty("summary")
	@NotNull
	private Summary summary;
	@JsonProperty("notification")
	@NotNull
	private Notification notification;
	@JsonProperty("persistent")
	private boolean persistent;
	@JsonProperty("toast")
	private boolean toast;
	@JsonProperty("display_type")
	private NotificationDisplayType displayType;
}
