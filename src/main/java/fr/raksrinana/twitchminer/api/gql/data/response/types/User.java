package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.UnknownDeserializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("User")
@Getter
public class User extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("displayName")
	@Nullable
	private String displayName;
	@JsonProperty("stream")
	@Nullable
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object stream;
	@JsonProperty("self")
	@Nullable
	private UserSelfConnection self;
	@JsonProperty("channel")
	private Channel channel;
	@JsonProperty("communityPoints")
	private CommunityPointsUserProperties communityPoints;
	
	public User(){
		super("User");
	}
}
