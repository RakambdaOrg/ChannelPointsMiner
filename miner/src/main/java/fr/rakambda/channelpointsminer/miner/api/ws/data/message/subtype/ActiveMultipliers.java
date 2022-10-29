package fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ActiveMultipliers{
	@JsonProperty("user_id")
	@NotNull
	private String userId;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
	@JsonProperty("multipliers")
	@NotNull
	@Builder.Default
	private List<CommunityPointsMultiplier> multipliers = new ArrayList<>();
}
