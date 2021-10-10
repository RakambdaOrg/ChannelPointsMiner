package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("CommunityPointsMultiplier")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsMultiplier extends GQLType{
	@JsonProperty("reasonCode")
	@NotNull
	private MultiplierReasonCode reasonCode;
	@JsonProperty("factor")
	private float factor;
}
