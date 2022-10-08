package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
