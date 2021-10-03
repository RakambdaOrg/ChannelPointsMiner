package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsMultiplier")
@Getter
@AllArgsConstructor
public class CommunityPointsMultiplier extends GQLType{
	@JsonProperty("reasonCode")
	private ReasonCode reasonCode;
	@JsonProperty("factor")
	private float factor;
	
	public CommunityPointsMultiplier(){
		super("CommunityPointsMultiplier");
	}
}
