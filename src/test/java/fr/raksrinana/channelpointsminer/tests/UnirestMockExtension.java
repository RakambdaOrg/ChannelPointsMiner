package fr.raksrinana.channelpointsminer.tests;

import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.*;
import java.util.Objects;

@Log4j2
public class UnirestMockExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver{
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
	}
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception{
		Unirest.config().clearDefaultHeaders();
		unirest = MockClient.register();
	}
	
	@Override
	public void afterEach(ExtensionContext context){
		MockClient.clear();
	}
	
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return Objects.equals(parameterContext.getParameter().getType(), MockClient.class);
	}
	
	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return unirest;
	}
}
