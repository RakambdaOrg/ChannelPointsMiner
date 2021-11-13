package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("UserSelfConnection")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserSelfConnection extends GQLType{
	@JsonProperty("isModerator")
	private boolean moderator;
	@JsonProperty("canFollow")
	private boolean canFollow;
	@JsonProperty("friendship")
	@JsonDeserialize(using = UnknownDeserializer.class)
	@Nullable
	private Object friendship;
	@JsonProperty("follower")
	@Nullable
	private FollowerEdge follower;
}
