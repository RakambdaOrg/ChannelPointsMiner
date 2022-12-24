package fr.rakambda.channelpointsminer.miner.tests;

import io.netty.util.internal.ThreadLocalRandom;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import java.util.Objects;

public class WebsocketMockServerExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterAllCallback, ParameterResolver{
	private final WebsocketMockServer server;
	
	public WebsocketMockServerExtension(){
		this(ThreadLocalRandom.current().nextInt(10000, 30000));
	}
	
	public WebsocketMockServerExtension(int port){
		server = new WebsocketMockServer(port);
	}
	
	@Override
	public void beforeAll(ExtensionContext context){
		server.start();
	}
	
	@Override
	public void beforeEach(ExtensionContext context){
		server.reset();
	}
	
	@Override
	public void afterAll(ExtensionContext context) throws InterruptedException{
		server.stop(10000);
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
