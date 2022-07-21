package fr.raksrinana.channelpointsminer.miner.util;

public class SleepHandler{
	public static void sleep(long delay) throws InterruptedException{
		if(delay > 0){
			Thread.sleep(delay);
		}
	}
}
