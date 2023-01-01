package fr.rakambda.channelpointsminer.miner.api.ws.data.message.readnotifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class ReadNotificationsData{
	@JsonProperty("notification_ids")
	@NotNull
	private List<String> notificationIds = new LinkedList<>();
	@JsonProperty("display_type")
	@NotNull
	private String displayType;
	@JsonProperty("summary")
	@NotNull
	private NotificationSummary summary;
}
