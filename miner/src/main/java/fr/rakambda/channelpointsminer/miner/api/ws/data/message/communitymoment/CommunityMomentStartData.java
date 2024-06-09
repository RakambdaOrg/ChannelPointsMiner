package fr.rakambda.channelpointsminer.miner.api.ws.data.message.communitymoment;

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
public class CommunityMomentStartData{
	@JsonProperty("moment_id")
	@NotNull
	private String momentId;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
}
