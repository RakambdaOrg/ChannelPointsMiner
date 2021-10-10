package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("CommunityPointsMultiplier")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsMultiplier extends GQLType{
	@JsonProperty("reasonCode")
	private MultiplierReasonCode reasonCode;
	@JsonProperty("factor")
	private float factor;
}
