package fr.raksrinana.channelpointsminer.api.ws.data.message.deletenotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.NotificationDisplayType;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.NotificationSummary;
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
public class DeleteNotificationData{
	@JsonProperty("notification_id")
	@NotNull
	private String notificationId;
	@JsonProperty("summary")
	@NotNull
	private NotificationSummary summary;
	@JsonProperty("display_type")
	@NotNull
	private NotificationDisplayType displayType;
}
