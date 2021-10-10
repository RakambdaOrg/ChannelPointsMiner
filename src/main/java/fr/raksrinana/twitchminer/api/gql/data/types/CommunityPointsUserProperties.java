package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import java.util.List;

@JsonTypeName("CommunityPointsUserProperties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsUserProperties extends GQLType{
	@JsonProperty("lastViewedContent")
	private List<CommunityPointsLastViewedContentByTypeAndID> lastViewedContent;
}
