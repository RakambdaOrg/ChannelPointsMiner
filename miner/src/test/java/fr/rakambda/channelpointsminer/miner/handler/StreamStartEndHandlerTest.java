package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.withislive.WithIsStreamLiveData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.BroadcastSettingsUpdate;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamDown;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamUp;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class StreamStartEndHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_NAME = "streamer-name";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	@InjectMocks
	private StreamStartEndHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private Streamer streamer;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private Topic topic;
	@Mock
	private StreamUp streamUpMessage;
	@Mock
	private StreamDown streamDownMessage;
	@Mock
	private BroadcastSettingsUpdate broadcastSettingsUpdateMessage;
	@Mock
	private ITwitchChatClient chatClient;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private GQLResponse<WithIsStreamLiveData> withIsStreamLiveResponse;
	@Mock
	private WithIsStreamLiveData withIsStreamLiveData;
	@Mock
	private User user;
	@Mock
	private Stream stream;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_NAME);
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamer.isStreaming()).thenReturn(false);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(miner.getChatClient()).thenReturn(chatClient);
		lenient().when(streamUpMessage.getServerTime()).thenReturn(NOW);
		lenient().when(streamDownMessage.getServerTime()).thenReturn(NOW);
		lenient().when(broadcastSettingsUpdateMessage.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(gqlApi.withIsStreamLive(STREAMER_ID)).thenReturn(Optional.of(withIsStreamLiveResponse));
		lenient().when(withIsStreamLiveResponse.getData()).thenReturn(withIsStreamLiveData);
		lenient().when(withIsStreamLiveData.getUser()).thenReturn(user);
		lenient().when(user.getStream()).thenReturn(stream);
	}
	
	@Test
	void streamUp(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(eventManager).onEvent(new StreamUpEvent(STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient, never()).join(any());
	}
	
	@Test
	void streamUpWithJoinIrc(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(eventManager).onEvent(new StreamUpEvent(STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient).join(STREAMER_NAME);
	}
	
	@Test
	void streamUpUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(eventManager).onEvent(new StreamUpEvent(STREAMER_ID, null, null, NOW));
		verify(chatClient, never()).join(any());
	}
	
	@Test
	void streamDown(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.handle(topic, streamDownMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(eventManager).onEvent(new StreamDownEvent(STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient).leave(STREAMER_NAME);
	}
	
	@Test
	void streamDownUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamDownMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(eventManager).onEvent(new StreamDownEvent(STREAMER_ID, null, null, NOW));
		verify(chatClient, never()).leave(any());
	}
	
	@Test
	void broadcastSettingsUpdateAndNotStreamingFromGql(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
				var runnable = invocation.getArgument(0, Runnable.class);
				runnable.run();
				return null;
			});
			
			when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
			when(user.getStream()).thenReturn(null);
			
			assertDoesNotThrow(() -> tested.handle(topic, broadcastSettingsUpdateMessage));
			
			verify(miner).updateStreamerInfos(streamer);
			verify(eventManager).onEvent(new StreamDownEvent(STREAMER_ID, STREAMER_NAME, streamer, NOW));
			verify(chatClient).leave(STREAMER_NAME);
		}
	}
	
	@Test
	void broadcastSettingsUpdateAndStreamingFromGql(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
				var runnable = invocation.getArgument(0, Runnable.class);
				runnable.run();
				return null;
			});
			
			when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
			
			assertDoesNotThrow(() -> tested.handle(topic, broadcastSettingsUpdateMessage));
			
			verify(miner).updateStreamerInfos(streamer);
			verify(eventManager).onEvent(new StreamUpEvent(STREAMER_ID, STREAMER_NAME, streamer, NOW));
			verify(chatClient, never()).join(any());
		}
	}
	
	@Test
	void broadcastSettingsUpdateAndNotStreamingFromMemory(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
				var runnable = invocation.getArgument(0, Runnable.class);
				runnable.run();
				return null;
			});
			
			when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
			when(gqlApi.withIsStreamLive(STREAMER_ID)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.handle(topic, broadcastSettingsUpdateMessage));
			
			verify(miner).updateStreamerInfos(streamer);
			verify(eventManager, never()).onEvent(any());
			verify(chatClient, never()).join(any());
		}
	}
	
	@Test
	void broadcastSettingsUpdateAndStreamingFromMemory(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
				var runnable = invocation.getArgument(0, Runnable.class);
				runnable.run();
				return null;
			});
			
			when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
			when(gqlApi.withIsStreamLive(STREAMER_ID)).thenReturn(Optional.empty());
			when(streamer.isStreaming()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.handle(topic, broadcastSettingsUpdateMessage));
			
			verify(miner).updateStreamerInfos(streamer);
			verify(eventManager, never()).onEvent(any());
			verify(chatClient, never()).leave(any());
		}
	}
	
	@Test
	void broadcastSettingsUpdateAndUnknown(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
			when(gqlApi.withIsStreamLive(STREAMER_ID)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.handle(topic, broadcastSettingsUpdateMessage));
			
			verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
			verify(eventManager, never()).onEvent(any());
			verify(chatClient, never()).leave(STREAMER_NAME);
		}
	}
}