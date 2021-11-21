package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class AbstractLogEventTest{
	private AbstractLogEvent tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		tested = new AbstractLogEvent(miner, streamer){
			@Override
			protected String getEmoji(){
				return "emoji";
			}
			
			@Override
			protected int getEmbedColor(){
				return 24;
			}
			
			@Override
			protected String getEmbedDescription(){
				return getAsLog();
			}
			
			@Override
			public String getAsLog(){
				return "value " + millify(1000, true);
			}
		};
	}
	
	@Test
	void millifyIsThreadSafe(){
		var executor = Executors.newFixedThreadPool(4);
		try{
			var futures = IntStream.range(0, 1000000)
					.mapToObj(i -> executor.submit(() -> tested.getAsLog()))
					.toList();
			
			futures.forEach(future -> {
				try{
					var value = future.get(1, MINUTES);
					assertThat(value).isEqualTo("value +1K");
				}
				catch(InterruptedException | ExecutionException | TimeoutException e){
					fail("Failed to wait for value", e);
				}
			});
		}
		finally{
			executor.shutdownNow();
		}
	}
}