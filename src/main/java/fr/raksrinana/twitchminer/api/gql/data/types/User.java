package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@JsonTypeName("User")
@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class User extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("displayName")
	@Nullable
	private String displayName;
	@JsonProperty("login")
	private String login;
	@JsonProperty("stream")
	@Nullable
	private Stream stream;
	@JsonProperty("self")
	@Nullable
	private UserSelfConnection self;
	@JsonProperty("channel")
	private Channel channel;
	@JsonProperty("communityPoints")
	private CommunityPointsUserProperties communityPoints;
	@JsonProperty("profileURL")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL profileUrl;
	@JsonProperty("profileImageURL")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL profileImageUrl;
	@JsonProperty("broadcastSettings")
	private BroadcastSettings broadcastSettings;
	
	public User(){
		super("User");
	}
	
	public boolean isStreaming(){
		return Objects.nonNull(getStream());
	}
	
	public Optional<Game> getGame(){
		return Optional.ofNullable(broadcastSettings)
				.map(BroadcastSettings::getGame);
	}
}
