package fr.rakambda.channelpointsminer.miner.log;

import kong.unirest.core.Config;
import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class UnirestLogger implements Interceptor{
	private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
	private static final String M3U8_URL = "https://usher.ttvnw.net/api/channel/hls/";
	private static final String M3U8_CHUNK_URL = "https://video-weaver.";
	
	@Override
	public void onRequest(HttpRequest<?> request, Config config){
		log.trace("Sending request to {}", request.getUrl());
	}
	
	@Override
	public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config){
		if(response.isSuccess()){
			log.trace("Received successful response for {} with statusCode `{}`", request.getUrl(), response.getStatus());
			return;
		}
		
		if(response.getStatus() == 429){
			log.warn("Failed to request {} got statusCode `{}`", request.getUrl(), response.getStatus());
			return;
		}
		
		if(shouldLogError(request, response)){
			log.error("Failed to request {} got statusCode `{}` and parsing error: {}", request.getUrl(), response.getStatus(), response.getParsingError());
			response.getParsingError().ifPresent(ex -> log.error("Failed to parse body: {}", ex.getOriginalBody()));
		}
	}
	
	private static boolean shouldLogError(@NotNull HttpRequestSummary request, @NotNull HttpResponse<?> response){
		if(response.getParsingError().isPresent() && response.getStatus() == 204){
			return false;
		}
		
		if(request.getUrl().equals(TOKEN_URL) && response.getStatus() == 400){
			return false;
		}
		
		if(request.getUrl().startsWith(M3U8_URL) && response.getStatus() == 403){
			return false;
		}
		
		if(request.getUrl().startsWith(M3U8_CHUNK_URL) && response.getStatus() == 403){
			return false;
		}
		
		return true;
	}
}
