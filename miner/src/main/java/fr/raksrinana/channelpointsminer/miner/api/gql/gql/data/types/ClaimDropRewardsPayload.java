package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("ClaimDropRewardsPayload")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimDropRewardsPayload extends GQLType{
	@JsonProperty("isUserAccountConnected")
	private boolean isUserAccountConnected;
	@JsonProperty("status")
	private ClaimDropRewardsStatus status;
}
