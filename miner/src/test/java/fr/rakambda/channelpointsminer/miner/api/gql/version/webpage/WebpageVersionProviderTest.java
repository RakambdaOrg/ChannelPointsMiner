package fr.rakambda.channelpointsminer.miner.api.gql.version.webpage;

import fr.rakambda.channelpointsminer.miner.api.gql.version.VersionException;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static kong.unirest.core.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class WebpageVersionProviderTest{
    private WebpageVersionProvider tested;
    
    private UnirestMock unirestMock;
    
    @BeforeEach
    void setUp(UnirestMock unirestMock){
        this.unirestMock = unirestMock;
        
        tested = new WebpageVersionProvider(unirestMock.getUnirestInstance());
    }
    
    @Test
    void clientVersionErrorResponse(){
        expectClientVersionRequest(500, null);
        
        assertThrows(VersionException.class, () -> tested.getVersion());
        unirestMock.verifyAll();
    }
    
    @Test
    void clientVersionNullBody(){
        expectClientVersionRequest(200, null);
        
        assertThrows(VersionException.class, () -> tested.getVersion());
        unirestMock.verifyAll();
    }
    
    @Test
    void clientVersionNotMatchingBody(){
        expectClientVersionRequest(200, "not what we want");
        
        assertThrows(VersionException.class, () -> tested.getVersion());
        unirestMock.verifyAll();
    }
    
    @Test
    void clientVersionChanged() throws VersionException{
        var version = "0202fcd9-207a-4659-956c-ed2030260de0";
        setupClientVersionOk(version);
        
        assertThat(tested.getVersion()).isEqualTo(version);
        unirestMock.verifyAll();
    }
    
    private void setupClientVersionOk(String version){
        expectClientVersionRequest(200, "window.__twilightBuildID=\"%s\";".formatted(version));
    }
    
    private void expectClientVersionRequest(int responseStatus, String responseBody){
        unirestMock.expect(GET, "https://www.twitch.tv")
                .thenReturn(responseBody)
                .withStatus(responseStatus);
    }
}