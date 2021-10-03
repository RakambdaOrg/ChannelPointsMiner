package fr.raksrinana.twitchminer.api.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.utils.json.CookieDeserializer;
import fr.raksrinana.twitchminer.utils.json.CookieSerializer;
import kong.unirest.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwitchLogin{
	private static final String PERSISTENT_COOKIE_NAME = "persistent";
	
	@Getter
	@JsonProperty("username")
	private String username;
	@Getter
	@JsonProperty("accessToken")
	private String accessToken;
	@JsonProperty("cookies")
	@JsonDeserialize(contentUsing = CookieDeserializer.class)
	@JsonSerialize(contentUsing = CookieSerializer.class)
	private List<Cookie> cookies;
	@JsonProperty("userId")
	private String userId;
	
	public String getUserId(){
		if(Objects.isNull(userId)){
			userId = cookies.stream()
					.filter(c -> Objects.equals(c.getName(), PERSISTENT_COOKIE_NAME))
					.findAny()
					.map(Cookie::getValue)
					.map(v -> v.split("%")[0])
					.or(() -> GQLApi.reportMenuItem(username)
							.map(GQLResponse::getData)
							.map(ReportMenuItemData::getUser)
							.map(User::getId))
					.orElseThrow(() -> new IllegalStateException("Failed to get current user id"));
		}
		return userId;
	}
}
