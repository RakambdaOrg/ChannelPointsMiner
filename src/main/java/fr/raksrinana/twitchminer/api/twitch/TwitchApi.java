package fr.raksrinana.twitchminer.api.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
public class TwitchApi{
	private static final Pattern SETTINGS_URL_PATTERN = Pattern.compile("https://static.twitchcdn.net/config/settings.*?js");
	private static final Pattern SPADE_URL_PATTERN = Pattern.compile("\"spade_url\":\"(.*?)\"");
	
	@NotNull
	public static Optional<URL> getSpadeUrl(@NotNull URL streamerUrl){
		return getSettingsUrl(streamerUrl)
				.map(settingsUrl -> {
					var response = Unirest.get(settingsUrl.toString()).asString();
					
					if(!response.isSuccess()){
						return null;
					}
					
					var matcher = SPADE_URL_PATTERN.matcher(response.getBody());
					if(!matcher.find()){
						log.error("Failed to get spade url");
						return null;
					}
					
					try{
						return URI.create(matcher.group(1)).toURL();
					}
					catch(MalformedURLException e){
						log.error("Failed to parse spade url", e);
						return null;
					}
				});
	}
	
	@NotNull
	private static Optional<URL> getSettingsUrl(@NotNull URL streamerUrl){
		var response = Unirest.get(streamerUrl.toString()).asString();
		
		if(!response.isSuccess()){
			return Optional.empty();
		}
		
		var matcher = SETTINGS_URL_PATTERN.matcher(response.getBody());
		if(!matcher.find()){
			log.error("Failed to get settings url");
			return Optional.empty();
		}
		
		try{
			return Optional.of(URI.create(matcher.group()).toURL());
		}
		catch(MalformedURLException e){
			log.error("Failed to parse settings url", e);
			return Optional.empty();
		}
	}
	
	public static boolean sendMinutesWatched(@NotNull URL spadeUrl, @NotNull MinuteWatchedRequest request){
		try{
			var requestStr = JacksonUtils.writeAsString(List.of(request));
			var requestBase64 = new String(Base64.getEncoder().encode(requestStr.getBytes(UTF_8)), UTF_8);
			
			var response = Unirest.post(spadeUrl.toString())
					.body(new JSONObject(Map.of("data", requestBase64)))
					.asEmpty();
			
			return response.isSuccess();
		}
		catch(JsonProcessingException e){
			log.error("Failed to send minute watched", e);
			return false;
		}
	}
}
