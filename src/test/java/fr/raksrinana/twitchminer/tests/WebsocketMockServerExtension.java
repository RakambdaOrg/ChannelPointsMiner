package fr.raksrinana.twitchminer.tests;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.*;
import java.util.Objects;

@Log4j2
public class WebsocketMockServerExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterAllCallback, ParameterResolver{
	public static final int PORT = 8547;
	
	private final WebsocketMockServer server;
	
	public WebsocketMockServerExtension(){
		server = new WebsocketMockServer(PORT);
	}
	
	@Override
	public void beforeAll(ExtensionContext context){
		server.start();
	}
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception{
		server.reset();
	}
	
	@Override
	public void afterAll(ExtensionContext context) throws InterruptedException{
		server.stop(1000);
	}
	
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return Objects.equals(parameterContext.getParameter().getType(), WebsocketMockServer.class);
	}
	
	@Override
	public WebsocketMockServer resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
		return server;
	}
}
