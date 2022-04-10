package fr.raksrinana.channelpointsminer.miner.api.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.miner.util.json.CookieDeserializer;
import fr.raksrinana.channelpointsminer.miner.util.json.CookieSerializer;
import kong.unirest.core.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	@NotNull
	@ToString.Include
	private String username;
	@Getter
	@JsonProperty("accessToken")
	@NotNull
	private String accessToken;
	@JsonProperty("cookies")
	@JsonDeserialize(contentUsing = CookieDeserializer.class)
	@JsonSerialize(contentUsing = CookieSerializer.class)
	@Builder.Default
	@NotNull
	private List<Cookie> cookies = new ArrayList<>();
	@JsonProperty("userId")
	@Nullable
	@ToString.Include
	private String userId;
	
	public int getUserIdAsInt(){
		return Integer.parseInt(fetchUserId());
	}
	
	@NotNull
	public String fetchUserId(){
		if(Objects.isNull(userId)){
			userId = cookies.stream()
					.filter(c -> Objects.equals(c.getName(), PERSISTENT_COOKIE_NAME))
					.findAny()
					.map(Cookie::getValue)
					.map(v -> v.split("%")[0])
					.or(() -> ApiFactory.createGqlApi(this).reportMenuItem(username)
							.map(GQLResponse::getData)
							.map(ReportMenuItemData::getUser)
							.map(User::getId))
					.orElseThrow(() -> new IllegalStateException("Failed to get current user id"));
		}
		return userId;
	}
}
