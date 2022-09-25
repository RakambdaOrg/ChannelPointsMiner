package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("ClaimCommunityPointsPayload")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimCommunityPointsPayload extends GQLType{
	@JsonProperty("claim")
	@Nullable
	private CommunityPointsClaim claim;
	@JsonProperty("currentPoints")
	@Nullable
	private Integer currentPoints;
	@JsonProperty("error")
	@Nullable
	private ClaimCommunityPointsError error;
}
