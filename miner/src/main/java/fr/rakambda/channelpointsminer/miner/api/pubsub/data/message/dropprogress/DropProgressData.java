package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.dropprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class DropProgressData{
	@JsonProperty("channel_id")
	@NonNull
	private String channelId;
	@JsonProperty("current_progress_min")
	private int currentProgressMin;
	@JsonProperty("required_progress_min")
	private int requiredProgressMin;
}
