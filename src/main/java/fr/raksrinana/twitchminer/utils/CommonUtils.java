package fr.raksrinana.twitchminer.utils;

import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils{
	public static void randomSleep(long delay, long delta) throws InterruptedException{
		long actualDelay = delay - delta / 2 + ThreadLocalRandom.current().nextLong(delta);
		if(actualDelay > 0){
			Thread.sleep(actualDelay);
		}
	}
}
