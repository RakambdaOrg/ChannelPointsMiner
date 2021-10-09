package fr.raksrinana.twitchminer.utils;

import org.junit.jupiter.api.RepeatedTest;
import java.time.Duration;
import static org.awaitility.Awaitility.await;

class CommonUtilsTest{
	@RepeatedTest(5)
	void sleepInRange(){
		await().atLeast(Duration.ofMillis(250)).atMost(Duration.ofMillis(750)).until(() -> {
			CommonUtils.randomSleep(500, 200);
			return true;
		});
	}
}