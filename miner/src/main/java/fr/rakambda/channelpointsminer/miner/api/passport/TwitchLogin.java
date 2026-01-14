package fr.rakambda.channelpointsminer.miner.api.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.GetUserIdFromLoginData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.util.json.CookieDeserializer;
import fr.rakambda.channelpointsminer.miner.util.json.CookieSerializer;
import kong.unirest.core.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class TwitchLogin{
	private static final String PERSISTENT_COOKIE_NAME = "persistent";
	
	@Getter
	@JsonProperty("username")
	@NonNull
	@ToString.Include
	private String username;
	@Getter
	@JsonProperty("accessToken")
	@NonNull
	private String accessToken;
	@JsonProperty("cookies")
	@JsonDeserialize(contentUsing = CookieDeserializer.class)
	@JsonSerialize(contentUsing = CookieSerializer.class)
	@Builder.Default
	@NonNull
	@EqualsAndHashCode.Exclude
	private List<Cookie> cookies = new ArrayList<>();
	@JsonProperty("userId")
	@Nullable
	@ToString.Include
	private String userId;
	@JsonProperty("twitchClient")
	@NonNull
	@Getter
	private TwitchClient twitchClient;
	
	public int getUserIdAsInt(@NonNull GQLApi gqlApi){
		return Integer.parseInt(fetchUserId(gqlApi));
	}
	
	@NonNull
	public String fetchUserId(@NonNull GQLApi gqlApi){
		if(Objects.isNull(userId)){
			userId = cookies.stream()
					.filter(c -> Objects.equals(c.getName(), PERSISTENT_COOKIE_NAME))
					.findAny()
					.map(Cookie::getValue)
					.map(v -> v.split("%")[0])
					.or(() -> gqlApi.getUserIdFromLogin(username)
							.map(GQLResponse::getData)
							.map(GetUserIdFromLoginData::getUser)
							.map(User::getId))
					.orElseThrow(() -> new IllegalStateException("Failed to get current user id"));
		}
		return userId;
	}
}
