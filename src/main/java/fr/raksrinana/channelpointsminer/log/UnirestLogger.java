package fr.raksrinana.channelpointsminer.log;

import kong.unirest.core.Config;
import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UnirestLogger implements Interceptor{
	@Override
	public void onRequest(HttpRequest<?> request, Config config){
		log.trace("Sending request to {}", request.getUrl());
	}
	
	@Override
	public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config){
		if(!response.isSuccess() && !(response.getParsingError().isPresent() && response.getStatus() == 204)){
			log.error("Failed to request {} got statusCode `{}` and parsing error: {}", request.getUrl(), response.getStatus(), response.getParsingError());
			response.getParsingError().ifPresent(ex -> log.error("Failed to parse body: {}", ex.getOriginalBody()));
		}
		else{
			log.trace("Received successful response for {} with statusCode `{}`", request.getUrl(), response.getStatus());
		}
	}
}
