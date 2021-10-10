package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.URLDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@JsonTypeName("User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("displayName")
	@Nullable
	private String displayName;
	@JsonProperty("login")
	@Nullable
	private String login;
	@JsonProperty("stream")
	@Nullable
	private Stream stream;
	@JsonProperty("self")
	@Nullable
	private UserSelfConnection self;
	@JsonProperty("channel")
	@Nullable
	private Channel channel;
	@JsonProperty("communityPoints")
	@Nullable
	private CommunityPointsUserProperties communityPoints;
	@JsonProperty("profileURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL profileUrl;
	@JsonProperty("profileImageURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL profileImageUrl;
	@JsonProperty("broadcastSettings")
	@Nullable
	private BroadcastSettings broadcastSettings;
	@JsonProperty("inventory")
	@Nullable
	private Inventory inventory;
	
	public boolean isStreaming(){
		return Objects.nonNull(stream);
	}
	
	public Optional<Game> getGame(){
		return Optional.ofNullable(broadcastSettings)
				.map(BroadcastSettings::getGame);
	}
}
