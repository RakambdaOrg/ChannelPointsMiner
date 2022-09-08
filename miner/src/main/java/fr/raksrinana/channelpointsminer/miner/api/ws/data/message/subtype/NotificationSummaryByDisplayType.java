package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
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
public class NotificationSummaryByDisplayType{
	@JsonProperty("unread_summary")
	private Summary unreadSummary;
	@JsonProperty("unseen_summary")
	private Summary unseenSummary;
}
