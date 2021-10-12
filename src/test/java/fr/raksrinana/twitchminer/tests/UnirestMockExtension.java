package fr.raksrinana.twitchminer.tests;

import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.*;

@Log4j2
public class UnirestMockExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterAllCallback{
	@Getter
	private MockClient unirest;
	
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
		
		unirest = MockClient.register();
	}
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception{
		Unirest.config().clearDefaultHeaders();
	}
	
	@Override
	public void afterAll(ExtensionContext context){
		MockClient.clear();
	}
	
	public void verifyAll(){
		unirest.verifyAll();
	}
	
	public Expectation expect(HttpMethod post, String url){
		return unirest.expect(post, url);
	}
}
