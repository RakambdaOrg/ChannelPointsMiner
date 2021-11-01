package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

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
	private int claimCount;
}
