package fr.rakambda.channelpointsminer.miner.api.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.PlayerEvent;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Log4j2
public class TwitchApi{
	private static final Pattern SETTINGS_URL_PATTERN = Pattern.compile("(https://static.twitchcdn.net/config/settings.*?js|https://assets.twitch.tv/config/settings.*?.js)");
	private static final Pattern SPADE_URL_PATTERN = Pattern.compile("\"spade(Url|_url)\":\"(.*?)\"");
	private static final Pattern M3U8_STREAM_PATTERN = Pattern.compile("(https://[/\\-.:\\\\,\"=\\w]+\\.m3u8)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern M3U8_CHUNK_PATTERN = Pattern.compile("^(https://[/\\-.:\\\\,\"=\\w]+\\.ts(\\?[.\\w\\-/=&]+)?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	private final UnirestInstance unirest;
	
	@NotNull
	public Optional<URL> getSpadeUrl(@NotNull URL streamerUrl){
		return getStreamerPageContent(streamerUrl).flatMap(this::getSpadeUrlFromContent);
	}
	
	private Optional<URL> getSpadeUrlFromContent(@NotNull String content){
		var result = extractUrl(SPADE_URL_PATTERN, 2, content).or(() -> extractSpadeFromSettings(content));
		if(result.isEmpty()){
			log.error("Failed to get Spade URL, content was : {}", content);
		}
		return result;
	}
	
	@NotNull
	private Optional<String> getStreamerPageContent(@NotNull URL streamerUrl){
		var response = unirest.get(streamerUrl.toString()).asString();
		
		if(!response.isSuccess()){
			log.warn("Failed to get streamer page content");
			return Optional.empty();
		}
		
		return Optional.of(response.getBody());
	}
	
	@NotNull
	private Optional<URL> extractSpadeFromSettings(@NotNull String content){
		var settings = extractUrl(SETTINGS_URL_PATTERN, 1, content)
				.map(settingsUrl -> {
					var response = unirest.get(settingsUrl.toString()).asString();
					
					if(!response.isSuccess()){
						log.warn("Failed to get spade settings from {}", settingsUrl);
						return null;
					}
					
					return response.getBody();
				});
		var result = settings.flatMap(c -> extractUrl(SPADE_URL_PATTERN, 2, c));
		
		if(result.isEmpty()){
			log.info("Spade settings : {}", settings);
		}
		return result;
	}
	
	@NotNull
	private Optional<URL> extractUrl(@NotNull Pattern pattern, int group, @NotNull String content){
		return extractUrl(pattern, group, content, false);
	}
	
	@NotNull
	private Optional<URL> extractUrl(@NotNull Pattern pattern, int group, @NotNull String content, boolean last){
		var matcher = pattern.matcher(content);
		var matched = false;
		String foundGroup = null;
		
		do{
			matched = matcher.find();
			if(matched){
				foundGroup = matcher.group(group);
			}
		}
		while(matched && last);
		
		if(Objects.isNull(foundGroup)){
			return Optional.empty();
		}
		
		try{
			return Optional.of(URI.create(foundGroup).toURL());
		}
		catch(MalformedURLException e){
			log.error("Failed to parse url", e);
			return Optional.empty();
		}
	}
	
	public boolean sendPlayerEvents(@NotNull URL spadeUrl, @NotNull PlayerEvent... events){
		try{
			var requestStr = JacksonUtils.writeAsString(events);
			var requestBase64 = new String(Base64.getEncoder().encode(requestStr.getBytes(UTF_8)), UTF_8);
			var data = "data=" + requestBase64;
			
			var response = unirest.post(spadeUrl.toString())
					.body(data)
					.asEmpty();
			
			return response.isSuccess();
		}
		catch(JsonProcessingException e){
			log.error("Failed to send minute watched", e);
			return false;
		}
	}
	
	@NotNull
	public Optional<URL> getM3u8Url(@NotNull String login, @NotNull String signature, @NotNull String value){
		var response = unirest.get("https://usher.ttvnw.net/api/channel/hls/%s.m3u8".formatted(login.toLowerCase(Locale.ROOT)))
				.queryString("sig", signature)
				.queryString("token", value)
				.queryString("cdm", "wv")
				.queryString("player_version", "1.22.0")
				.queryString("player_type", "pulsar")
				.queryString("player_backend", "mediaplayer")
				.queryString("playlist_include_framerate", "true")
				.queryString("allow_source", "true")
				.queryString("transcode_mode", "cbr_v1")
				.asString();
		
		if(!response.isSuccess()){
			if(response.getStatus() == 403){
				log.trace("Got 403 response for m3u8 content, is streamer region locked? (#783)");
				return Optional.empty();
			}
			
			log.error("Failed to get streamer M3U8 content");
			return Optional.empty();
		}
		
		return extractUrl(M3U8_STREAM_PATTERN, 1, response.getBody(), true);
	}
	
	public boolean openM3u8LastChunk(@NotNull URL m3u8Url){
		var playlistResponse = unirest.get(m3u8Url.toString()).asString();
		
		if(!playlistResponse.isSuccess()){
			if(playlistResponse.getStatus() == 403){
				log.trace("Got 403 response for m3u8 playlist, is streamer region locked? (#783)");
				return false;
			}
			
			log.error("Failed to get streamer M3U8 playlist");
			return false;
		}
		
		var chunkUrl = extractUrl(M3U8_CHUNK_PATTERN, 1, playlistResponse.getBody(), true);
		if(chunkUrl.isEmpty()){
			log.error("Failed to get streamer M3U8 chunk from playlist");
			return false;
		}
		
		var chunkRequest = unirest.get(chunkUrl.get().toString()).asBytes();
		return chunkRequest.isSuccess();
	}
}
