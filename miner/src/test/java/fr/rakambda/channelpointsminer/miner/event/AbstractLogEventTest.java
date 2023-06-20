package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.event.impl.MinerStartedEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.fail;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class AbstractLogEventTest{
	private AbstractLoggableEvent tested;
	
	@Mock
	private IMiner miner;
	
	@BeforeEach
	void setUp(){
		tested = new MinerStartedEvent("", "", "", Instant.now());
		tested.setMiner(miner);
	}
	
	@Test
	void millifyIsThreadSafe(){
		var executor = Executors.newFixedThreadPool(4);
		try{
			var futures = IntStream.range(0, 1000000)
					.mapToObj(i -> executor.submit(() -> tested.millify(1000, true)))
					.toList();
			
			futures.forEach(future -> {
				try{
					var value = future.get(1, MINUTES);
					Assertions.assertThat(value).isEqualTo("+1K");
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