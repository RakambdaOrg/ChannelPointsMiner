package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.URLDeserializer;
import fr.raksrinana.channelpointsminer.util.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

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
	@JsonProperty("bannerImageURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL bannerImageURL;
	@JsonProperty("broadcastSettings")
	@Nullable
	private BroadcastSettings broadcastSettings;
	@JsonProperty("inventory")
	@Nullable
	private Inventory inventory;
	@JsonProperty("follows")
	@Nullable
	private FollowConnection follows;
	@JsonProperty("activity")
	@JsonDeserialize(using = UnknownDeserializer.class)
	@Nullable
	private Object activity;
	@JsonProperty("availability")
	@JsonDeserialize(using = UnknownDeserializer.class)
	@Nullable
	private Object availability;
}
