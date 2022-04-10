package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.updatesummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummary;
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
public class UpdateSummaryData{
	@JsonProperty("summary")
	@NotNull
	private NotificationSummary summary;
}
