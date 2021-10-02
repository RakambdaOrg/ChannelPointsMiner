package fr.raksrinana.twitchminer.api.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.twitchminer.utils.json.CookieDeserializer;
import fr.raksrinana.twitchminer.utils.json.CookieSerializer;
import kong.unirest.Cookie;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersistentAuthentication{
	@JsonProperty("cookies")
	@JsonDeserialize(contentUsing = CookieDeserializer.class)
	@JsonSerialize(contentUsing = CookieSerializer.class)
	@Builder.Default
	private List<Cookie> cookies = new ArrayList<>();
	@JsonProperty("accessToken")
	private String accessToken;
}
