package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("DropCampaignSelfEdge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropCampaignSelfEdge extends GQLType{
	@JsonProperty("isAccountConnected")
	private boolean isAccountConnected;
}
