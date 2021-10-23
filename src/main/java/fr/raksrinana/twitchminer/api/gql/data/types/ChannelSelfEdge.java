package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Optional;

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
	
	@NotNull
	public Optional<CommunityPointsClaim> getClaim(){
		return Optional.ofNullable(communityPoints.getAvailableClaim());
	}
	
	@NotNull
	public Collection<CommunityPointsMultiplier> getMultipliers(){
		return communityPoints.getActiveMultipliers();
	}
}
