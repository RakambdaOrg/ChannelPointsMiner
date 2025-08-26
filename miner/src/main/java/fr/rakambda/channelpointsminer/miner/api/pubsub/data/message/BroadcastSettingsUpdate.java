package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("broadcast_settings_update")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastSettingsUpdate extends IPubSubMessage{
	@JsonProperty("channel_id")
	private String channelId;
}
