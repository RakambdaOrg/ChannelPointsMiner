package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChatRoomBanStatus;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiChatRoomBanStatusTest extends AbstractGQLTest{
    private static final String USERNAME = "username";
    private static final String CHANNEL = "channel";
    
    @Test
    void notBanned(){
        var expected = GQLResponse.<ChatRoomBanStatusData> builder()
                .extensions(Map.of(
                        "durationMilliseconds", 21,
                        "operationName", "ChatRoomBanStatus",
                        "requestID", "request-id"
                ))
                .data(ChatRoomBanStatusData.builder()
                        .build())
                .build();
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/channelRoomBanStatus_notBanned.json");
        
        assertThat(tested.chatRoomBanStatus(CHANNEL, USERNAME)).contains(expected);
        
        verifyAll();
    }
    
    @Test
    void banned(){
        var expected = GQLResponse.<ChatRoomBanStatusData> builder()
                .extensions(Map.of(
                        "durationMilliseconds", 21,
                        "operationName", "ChatRoomBanStatus",
                        "requestID", "request-id"
                ))
                .data(ChatRoomBanStatusData.builder()
		                .chatRoomBanStatus(ChatRoomBanStatus.builder()
				                .build())
                        .build())
                .build();
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/channelRoomBanStatus_banned.json");
        
        var actual = tested.chatRoomBanStatus(CHANNEL, USERNAME);
        assertThat(actual).contains(expected);
        
        verifyAll();
    }
    
    @Override
    protected String getValidRequest(){
        return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"319f2a9a3ac7ddecd7925944416c14b818b65676ab69da604460b68938d22bea\",\"version\":1}},\"operationName\":\"ChatRoomBanStatus\",\"variables\":{\"channelID\":\"%s\",\"targetUserID\":\"%s\"}}".formatted(CHANNEL, USERNAME);
    }
}