package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
	@JsonProperty("login")
	@Nullable
	private String login;
	@JsonProperty("stream")
	@Nullable
	private Stream stream;
	@JsonProperty("channel")
	@Nullable
	private Channel channel;
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
	@JsonProperty("follows")
	@Nullable
	private FollowConnection follows;
}
