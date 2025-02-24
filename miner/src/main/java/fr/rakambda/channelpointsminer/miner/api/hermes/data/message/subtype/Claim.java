package fr.rakambda.channelpointsminer.miner.api.hermes.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Claim{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
}
