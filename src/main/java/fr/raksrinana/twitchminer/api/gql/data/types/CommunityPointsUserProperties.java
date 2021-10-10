package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
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
	@NotNull
	@Builder.Default
	private List<CommunityPointsLastViewedContentByTypeAndID> lastViewedContent = new ArrayList<>();
}
