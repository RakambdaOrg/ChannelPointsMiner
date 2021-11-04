package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("ChannelSelfEdge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelSelfEdge extends GQLType{
	@JsonProperty("communityPoints")
	@NotNull
	private CommunityPointsProperties communityPoints;
}
