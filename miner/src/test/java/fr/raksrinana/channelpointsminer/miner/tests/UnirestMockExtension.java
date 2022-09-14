package fr.raksrinana.channelpointsminer.miner.tests;

import fr.raksrinana.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.Config;
import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import kong.unirest.core.MockClient;
import kong.unirest.core.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import java.util.Objects;

@Log4j2
public class UnirestMockExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver{
	private UnirestMock unirest;
	
	@Override
	public void beforeAll(ExtensionContext context){
		Unirest.config().reset()
				.clearDefaultHeaders()
				.setObjectMapper(new JacksonObjectMapper(JacksonUtils.getMapper()))
				.interceptor(new Interceptor(){
					@Override
					public void onRequest(HttpRequest<?> request, Config config){
					}
					
					@Override
					public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config){
						if(!response.isSuccess()){
							response.getParsingError().ifPresent(ex -> log.error("Failed to parse body: {}", ex.getOriginalBody(), ex));
						}
					}
				});
	}
	
	@Override
	public void beforeEach(ExtensionContext context){
		Unirest.config().clearDefaultHeaders();
		unirest = new UnirestMock();
	}
	
	@Override
	public void afterEach(ExtensionContext context){
		MockClient.clear();
	}
	
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return Objects.equals(parameterContext.getParameter().getType(), UnirestMock.class);
	}
	
	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return unirest;
	}
}
