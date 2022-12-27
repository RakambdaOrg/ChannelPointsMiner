package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DiscordEventListenerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	
	@Mock
	private IMiner miner;
	@Mock
	private DiscordApi discordApi;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		var streamerProfileUrl = new URL("https://streamer-image");
		var channelUrl = new URL("https://streamer");
		
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(miner.getUsername()).thenReturn(USERNAME);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getProfileImage()).thenReturn(Optional.of(streamerProfileUrl));
		lenient().when(streamer.getChannelUrl()).thenReturn(channelUrl);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void eventIsFiltered(){
		var event = mock(IEvent.class);
		
		var tested = new DiscordEventListener(discordApi, true, e -> false);
		tested.onEvent(event);
		
		verify(discordApi, never()).sendMessage(any());
	}
}