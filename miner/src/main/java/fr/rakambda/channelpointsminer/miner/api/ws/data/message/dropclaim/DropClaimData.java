package fr.rakambda.channelpointsminer.miner.api.ws.data.message.dropclaim;

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
public class DropClaimData{
	@JsonProperty("drop_instance_id")
	@NotNull
	private String dropInstanceId;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
}
