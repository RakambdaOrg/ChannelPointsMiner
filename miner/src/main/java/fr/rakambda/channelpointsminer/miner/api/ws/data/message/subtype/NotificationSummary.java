package fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class NotificationSummary{
	@JsonProperty("unseen_view_count")
	private int unseenViewCount;
	@JsonProperty("last_seen_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime lastSeenAt;
	@JsonProperty("viewer_unread_count")
	private int viewerUnreadCount;
	@JsonProperty("creator_unread_count")
	private int creatorUnreadCount;
	@NotNull
	@JsonProperty("summaries_by_display_type")
	@Builder.Default
	private Map<NotificationDisplayType, NotificationSummaryByDisplayType> summariesByDisplayType = new HashMap<>();
}
