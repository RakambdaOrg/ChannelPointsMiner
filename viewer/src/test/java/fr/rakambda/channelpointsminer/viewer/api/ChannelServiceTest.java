package fr.rakambda.channelpointsminer.viewer.api;

import fr.rakambda.channelpointsminer.viewer.api.data.ChannelData;
import fr.rakambda.channelpointsminer.viewer.repository.ChannelRepository;
import fr.rakambda.channelpointsminer.viewer.repository.entity.ChannelEntity;
import org.assertj.core.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest{
    private static final String CHANNEL_ID1 = "CID1";
    private static final String CHANNEL_USERNAME1 = "USER1";
    private static final String CHANNEL_ID2 = "CID2";
    private static final String CHANNEL_USERNAME2 = "USER2";
    
    @InjectMocks
    private ChannelService tested;
    
    @Mock
    private ChannelRepository channelRepository;
    
    @Mock
    private ChannelEntity channelEntity1;
    @Mock
    private ChannelEntity channelEntity2;
    
    @BeforeEach
    void setUp(){
        lenient().when(channelEntity1.getId()).thenReturn(CHANNEL_ID1);
        lenient().when(channelEntity1.getUsername()).thenReturn(CHANNEL_USERNAME1);
        
        lenient().when(channelEntity2.getId()).thenReturn(CHANNEL_ID2);
        lenient().when(channelEntity2.getUsername()).thenReturn(CHANNEL_USERNAME2);
    }
    
    @Test
    void testGetAllBalance(){
        when(channelRepository.findAll()).thenReturn(List.of(channelEntity1, channelEntity2));
	
	    Assertions.assertThat(tested.listAll()).containsExactlyInAnyOrder(
			    ChannelData.builder().id(CHANNEL_ID1).username(CHANNEL_USERNAME1).build(),
			    ChannelData.builder().id(CHANNEL_ID2).username(CHANNEL_USERNAME2).build()
	    );
    }
}