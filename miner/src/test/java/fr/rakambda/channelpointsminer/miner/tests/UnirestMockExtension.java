package fr.rakambda.channelpointsminer.miner.tests;

import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.Config;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import kong.unirest.core.MockClient;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public class UnirestMockExtension implements Extension, BeforeEachCallback, AfterEachCallback, ParameterResolver{
    private final Map<String, UnirestInstance> unirestInstance = new HashMap<>();
    private final Map<String, UnirestMock> unirestMock = new HashMap<>();
    
    @Override
    public void beforeEach(ExtensionContext context){
        var instance = Unirest.spawnInstance();
        instance.config().reset()
                .clearDefaultHeaders()
                .setObjectMapper(new JacksonObjectMapper(JacksonUtils.getMapper()))
                .interceptor(new Interceptor(){
                    @Override
                    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config){
                        if(!response.isSuccess()){
                            response.getParsingError().ifPresent(ex -> log.error("Failed to parse body: {}", ex.getOriginalBody(), ex));
                        }
                    }
                });
        
        unirestInstance.put(context.getUniqueId(), instance);
        unirestMock.put(context.getUniqueId(), new UnirestMock(instance));
    }
    
    @Override
    public void afterEach(ExtensionContext context){
        Optional.ofNullable(unirestInstance.get(context.getUniqueId())).ifPresent(MockClient::clear);
        unirestInstance.remove(context.getUniqueId());
        unirestMock.remove(context.getUniqueId());
    }
    
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
        return Objects.equals(parameterContext.getParameter().getType(), UnirestMock.class);
    }
    
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException{
        return unirestMock.get(extensionContext.getUniqueId());
    }
}
