package fr.rakambda.channelpointsminer.miner.api.twitch;

import fr.rakambda.channelpointsminer.miner.api.twitch.data.PlayerEvent;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.UnirestException;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import tools.jackson.core.JacksonException;
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
	private static final Pattern M3U8_CHUNK_PATTERN = Pattern.compile("^(https://[/\\-.:\\\\,\"=\\w]+\\.(ts|mp4)(\\?[.\\w\\-/=&]+)?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	private final UnirestInstance unirest;
	
	@NonNull
	public Optional<URL> getSpadeUrl(@NonNull URL streamerUrl){
		return getStreamerPageContent(streamerUrl).flatMap(this::getSpadeUrlFromContent);
	}
	
	private Optional<URL> getSpadeUrlFromContent(@NonNull String content){
		var result = extractUrl(SPADE_URL_PATTERN, 2, content).or(() -> extractSpadeFromSettings(content));
		if(result.isEmpty()){
			log.error("Failed to get Spade URL, content was : {}", content);
		}
		return result;
	}
	
	@NonNull
	private Optional<String> getStreamerPageContent(@NonNull URL streamerUrl){
		var response = unirest.get(streamerUrl.toString()).asString();
		
		if(!response.isSuccess()){
			log.warn("Failed to get streamer page content");
			return Optional.empty();
		}
		
		return Optional.of(response.getBody());
	}
	
	@NonNull
	private Optional<URL> extractSpadeFromSettings(@NonNull String content){
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
	
	@NonNull
	private Optional<URL> extractUrl(@NonNull Pattern pattern, int group, @NonNull String content){
		return extractUrl(pattern, group, content, false);
	}
	
	@NonNull
	private Optional<URL> extractUrl(@NonNull Pattern pattern, int group, @NonNull String content, boolean last){
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
	
	public boolean sendPlayerEvents(@NonNull URL spadeUrl, @NonNull PlayerEvent... events){
		try{
			var requestStr = JacksonUtils.writeAsString(events);
			var requestBase64 = new String(Base64.getEncoder().encode(requestStr.getBytes(UTF_8)), UTF_8);
			var data = "data=" + requestBase64;
			
			var response = unirest.post(spadeUrl.toString())
					.body(data)
					.asEmpty();
			
			return response.isSuccess();
		}
		catch(JacksonException e){
			log.error("Failed to send minute watched", e);
			return false;
		}
	}
	
	@NonNull
	public Optional<URL> getM3u8Url(@NonNull String login, @NonNull String signature, @NonNull String value){
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
	
	public boolean openM3u8LastChunk(@NonNull URL m3u8Url){
		try{
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
			
			var chunkRequest = unirest.head(chunkUrl.get().toString()).asBytes();
			return chunkRequest.isSuccess();
		}
		catch(UnirestException e){
			log.error("Failed to get streamer M3U8", e);
			return false;
		}
	}
}
