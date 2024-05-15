package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
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
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class SendMinutesWatchedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_NAME = "streamer-name";
	private static final Instant NOW = Instant.parse("2021-03-25T18:12:36Z");
	private static final int INDEX = 5;
	
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_NAME);
		lenient().when(streamer.isStreaming()).thenReturn(true);
		lenient().when(streamer.getIndex()).thenReturn(INDEX);
	}
	
	@Test
	void sendingMinutesWatched(){
		var tested = new Tester(miner, true, true);
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.checkCalled).isOne();
		assertThat(tested.sendCalled).isOne();
		
		verify(streamer, never()).addWatchedDuration(any());
	}
	
	@Test
	void sendingMinutesWatchedUpdatesMinutesWatched(){
		var tested = new Tester(miner, true, true);
		
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
			
			var delta = Duration.ofSeconds(30);
			timeFactory.when(TimeFactory::now).thenReturn(NOW.plus(delta));
			assertDoesNotThrow(tested::run);
			verify(streamer).addWatchedDuration(delta);
		}
	}
	
	@Test
	void sendingMinutesWatchedUpdatesMinutesWatchedResetIfNotPresentOnARound(){
		var tested = new Tester(miner, true, true);
		
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.getStreamers()).thenReturn(List.of(streamer));
			
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
			
			var delta = Duration.ofSeconds(30);
			timeFactory.when(TimeFactory::now).thenReturn(NOW.plus(delta));
			
			assertDoesNotThrow(tested::run);
			verify(streamer).addWatchedDuration(delta);
			clearInvocations(streamer);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			when(miner.getStreamers()).thenReturn(List.of());
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
			
			when(miner.getStreamers()).thenReturn(List.of(streamer));
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
			
			delta = Duration.ofSeconds(45);
			timeFactory.when(TimeFactory::now).thenReturn(NOW.plus(delta));
			when(miner.getStreamers()).thenReturn(List.of(streamer));
			assertDoesNotThrow(tested::run);
			verify(streamer).addWatchedDuration(delta);
		}
	}
	
	@Test
	void sendingMinutesWatchedDoesNotUpdateMinutesWatchedIfCallFailed(){
		var tested = new Tester(miner, true, true);
		
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
			
			tested.sendResult = false;
			
			var delta = Duration.ofSeconds(30);
			timeFactory.when(TimeFactory::now).thenReturn(NOW.plus(delta));
			assertDoesNotThrow(tested::run);
			verify(streamer, never()).addWatchedDuration(any());
		}
	}
	
	@Test
	void sendingMinutesWatchedNotStreaming(){
		var tested = new Tester(miner, true, true);
		
		when(streamer.isStreaming()).thenReturn(false);
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sendCalled).isZero();
		
		verify(streamer, never()).addWatchedDuration(any());
	}
	
	@Test
	void sendingMinutesWatchedCheckKo(){
		var tested = new Tester(miner, false, true);
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sendCalled).isZero();
		
		verify(streamer, never()).addWatchedDuration(any());
	}
	
	@Test
	void sendingMinutesWatchedChatBanned(){
		var tested = new Tester(miner, true, true);
		
		when(streamer.isChatBanned()).thenReturn(true);
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sendCalled).isZero();
		
		verify(streamer, never()).addWatchedDuration(any());
	}
	
	@Test
	void sendingMinutesWatchedSeveralStreamers(){
		var tested = new Tester(miner, true, true);
		
		var streamer2 = mock(Streamer.class);
		when(streamer2.getId()).thenReturn("s2");
		when(streamer2.getUsername()).thenReturn("sn2");
		when(streamer2.isStreaming()).thenReturn(true);
		
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer2));
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sendCalled).isEqualTo(2);
		
		verify(streamer, never()).addWatchedDuration(any());
		verify(streamer2, never()).addWatchedDuration(any());
	}
	
	@Test
	void sendingMinutesWatchedMaxTwoStreamers(){
		var tested = new Tester(miner, true, true);
		
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer, streamer, streamer));
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sendCalled).isEqualTo(2);
	}
	
	@Test
	void sendingMinutesWatchedException(){
		var tested = new Tester(miner, true, null);
		
		assertDoesNotThrow(tested::run);
	}
	
	@Test
	void sendingMinutesWatchedBestScores() throws MalformedURLException{
		var tested = new Tester(miner, true, true);
		
		var s1 = mock(Streamer.class);
		when(s1.isStreaming()).thenReturn(true);
		when(s1.getScore(miner)).thenReturn(10);
		
		var spade2 = new URL("https://spade2");
		var s2 = mock(Streamer.class);
		when(s2.getId()).thenReturn("s2");
		when(s2.getUsername()).thenReturn("sn2");
		when(s2.isStreaming()).thenReturn(true);
		when(s2.getScore(miner)).thenReturn(100);
		
		var s3 = mock(Streamer.class);
		when(s3.isStreaming()).thenReturn(true);
		when(s3.getScore(miner)).thenReturn(20);
		
		var spade4 = new URL("https://spade4");
		var s4 = mock(Streamer.class);
		when(s4.getId()).thenReturn("s4");
		when(s4.getUsername()).thenReturn("sn4");
		when(s4.isStreaming()).thenReturn(true);
		when(s4.getScore(miner)).thenReturn(50);
		
		when(miner.getStreamers()).thenReturn(List.of(s1, s2, s3, s4));
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sentStreamers).containsExactlyInAnyOrder(s2, s4);
	}
	
	@Test
	void sendingMinutesWatchedBestScoresEqualsPicksIndex() throws MalformedURLException{
		var tested = new Tester(miner, true, true);
		
		var s1 = mock(Streamer.class);
		when(s1.isStreaming()).thenReturn(true);
		when(s1.getScore(miner)).thenReturn(10);
		when(s1.getIndex()).thenReturn(1);
		
		var spade2 = new URL("https://spade2");
		var s2 = mock(Streamer.class);
		when(s2.getId()).thenReturn("s2");
		when(s2.getUsername()).thenReturn("sn2");
		when(s2.isStreaming()).thenReturn(true);
		when(s2.getIndex()).thenReturn(0);
		when(s2.getScore(miner)).thenReturn(10);
		
		var s3 = mock(Streamer.class);
		when(s3.isStreaming()).thenReturn(true);
		when(s3.getScore(miner)).thenReturn(10);
		when(s3.getIndex()).thenReturn(25);
		
		var spade4 = new URL("https://spade4");
		var s4 = mock(Streamer.class);
		when(s4.getId()).thenReturn("s4");
		when(s4.getUsername()).thenReturn("sn4");
		when(s4.isStreaming()).thenReturn(true);
		when(s4.getIndex()).thenReturn(-5);
		when(s4.getScore(miner)).thenReturn(10);
		
		when(miner.getStreamers()).thenReturn(List.of(s1, s2, s3, s4));
		
		assertDoesNotThrow(tested::run);
		assertThat(tested.sentStreamers).containsExactlyInAnyOrder(s2, s4);
	}
	
	private static class Tester extends SendMinutesWatched{
		private boolean checkResult;
		private Boolean sendResult;
		
		private int checkCalled = 0;
		private int sendCalled = 0;
		
		private List<Streamer> sentStreamers = new LinkedList<>();
		
		public Tester(IMiner miner, boolean checkResult, Boolean sendResult){
			super(miner);
			this.checkResult = checkResult;
			this.sendResult = sendResult;
		}
		
		@Override
		protected String getType(){
			return "tester";
		}
		
		@Override
		protected boolean checkStreamer(Streamer streamer){
			checkCalled++;
			return checkResult;
		}
		
		@Override
		protected boolean send(Streamer streamer){
			sendCalled++;
			sentStreamers.add(streamer);
			if(Objects.isNull(sendResult)){
				throw new IllegalStateException("For tests");
			}
			return sendResult;
		}
	}
}