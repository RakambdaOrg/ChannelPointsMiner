package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

@JsonTypeName("DropBenefitEdge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropBenefitEdge extends GQLType{
	@JsonProperty("benefit")
	private DropBenefit benefit;
	@JsonProperty("entitlementLimit")
	private int entitlementLimit;
	@JsonProperty("claimCount")
	@Nullable
	private Integer claimCount;
}
