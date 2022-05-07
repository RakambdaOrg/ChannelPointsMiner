package fr.raksrinana.channelpointsminer.miner.util;

import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.RepeatedTest;
import java.time.Duration;
import static org.awaitility.Awaitility.await;

@ParallelizableTest
class CommonUtilsTest{
	@RepeatedTest(5)
	void sleepInRange(){
		await().atLeast(Duration.ofMillis(200)).atMost(Duration.ofMillis(800)).until(() -> {
			CommonUtils.randomSleep(500, 200);
			return true;
		});
	}
	
	@RepeatedTest(5)
	void sleepInRangeNegative(){
		await().pollDelay(Duration.ofMillis(10)).atMost(Duration.ofMillis(50)).until(() -> {
			CommonUtils.randomSleep(-5, 1);
			return true;
		});
	}
}