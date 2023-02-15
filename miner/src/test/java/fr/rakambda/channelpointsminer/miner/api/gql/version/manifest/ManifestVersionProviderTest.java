package fr.rakambda.channelpointsminer.miner.api.gql.version.manifest;

import fr.rakambda.channelpointsminer.miner.api.gql.version.VersionException;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
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
class ManifestVersionProviderTest{
    private ManifestVersionProvider tested;
    
    private UnirestMock unirestMock;
    
    @BeforeEach
    void setUp(UnirestMock unirestMock){
        this.unirestMock = unirestMock;
        
        tested = new ManifestVersionProvider(unirestMock.getUnirestInstance());
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
        expectClientVersionRequest(200, TestUtils.getAllResourceContent("api/gql/version/manifest.json"));
        
        assertThat(tested.getVersion()).isEqualTo("build-id");
        unirestMock.verifyAll();
    }
    
    private void expectClientVersionRequest(int responseStatus, String responseBody){
        unirestMock.expect(GET, "https://static.twitchcdn.net/config/manifest.json")
                .queryString("v", "1")
                .thenReturn(responseBody)
                .withStatus(responseStatus);
    }
}