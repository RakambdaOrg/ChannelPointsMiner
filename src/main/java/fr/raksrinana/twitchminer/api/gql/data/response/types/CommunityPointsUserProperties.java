package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@JsonTypeName("CommunityPointsUserProperties")
@Getter
@AllArgsConstructor
public class CommunityPointsUserProperties extends GQLType{
	@JsonProperty("lastViewedContent")
	private List<CommunityPointsLastViewedContentByTypeAndID> lastViewedContent;
	
	public CommunityPointsUserProperties(){
		super("CommunityPointsUserProperties");
	}
}
