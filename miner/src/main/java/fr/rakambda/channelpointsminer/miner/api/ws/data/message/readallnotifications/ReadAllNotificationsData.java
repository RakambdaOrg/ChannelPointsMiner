package fr.rakambda.channelpointsminer.miner.api.ws.data.message.readallnotifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class ReadAllNotificationsData{
	@JsonProperty("notification_ids")
	@Nullable
	private List<String> notificationIds;
	@JsonProperty("display_type")
	@NotNull
	private String displayType;
	@JsonProperty("summary")
	@NotNull
	private NotificationSummary summary;
}
