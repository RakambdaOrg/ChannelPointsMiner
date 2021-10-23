package fr.raksrinana.twitchminer.util;

import org.junit.jupiter.api.RepeatedTest;
import java.time.Duration;
import static org.awaitility.Awaitility.await;

class CommonUtilsTest{
	@RepeatedTest(5)
	void sleepInRange(){
		await().atLeast(Duration.ofMillis(200)).atMost(Duration.ofMillis(800)).until(() -> {
			CommonUtils.randomSleep(500, 200);
			return true;
		});
	}
}