package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.communitymoment;

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
public class CommunityMomentStartData{
	@JsonProperty("moment_id")
	@NonNull
	private String momentId;
	@JsonProperty("channel_id")
	@NonNull
	private String channelId;
}
