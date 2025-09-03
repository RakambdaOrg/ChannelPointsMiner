package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.communitymoment.CommunityMomentStartData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@JsonTypeName("active")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMomentStart extends IPubSubMessage{
	@JsonProperty("data")
	@NonNull
	private CommunityMomentStartData data;
}
