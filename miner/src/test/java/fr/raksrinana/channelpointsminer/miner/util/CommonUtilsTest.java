package fr.raksrinana.channelpointsminer.miner.util;

import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class CommonUtilsTest{
	@Captor
	private ArgumentCaptor<Long> captor;
	
	@RepeatedTest(500)
	void sleepInRange(){
		try(var sleepHandler = mockStatic(SleepHandler.class)){
			sleepHandler.when(() -> SleepHandler.sleep(captor.capture())).then(Answers.RETURNS_DEFAULTS);
			
			CommonUtils.randomSleep(500, 200);
			
			var delay = captor.getValue();
			assertThat(delay).isNotNull().isBetween(300L, 700L);
		}
	}
	
	@RepeatedTest(50)
	void sleepInRangeNegative(){
		try(var sleepHandler = mockStatic(SleepHandler.class)){
			sleepHandler.when(() -> SleepHandler.sleep(captor.capture())).then(Answers.RETURNS_DEFAULTS);
			
			CommonUtils.randomSleep(-5, 1);
			
			var delay = captor.getValue();
			assertThat(delay).isNotNull().isBetween(-6L, -4L);
		}
	}
}