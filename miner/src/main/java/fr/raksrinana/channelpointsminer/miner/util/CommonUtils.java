package fr.raksrinana.channelpointsminer.miner.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CommonUtils{
	@SneakyThrows
	public static void randomSleep(long delay, long delta){
		long actualDelay = delay - delta / 2 + ThreadLocalRandom.current().nextLong(delta);
		if(actualDelay > 0){
			Thread.sleep(actualDelay);
		}
	}
	
	/**
	 * Get a user input.
	 *
	 * @param message The message to be displayed before asking input.
	 *
	 * @return User input.
	 */
	@NotNull
	public static String getUserInput(@NotNull String message){
		try{
			System.out.println(message);
			
			var scanner = new Scanner(System.in);
			return scanner.nextLine();
		}
		catch(NoSuchElementException e){
			throw new NoSuchElementException("No line was read from input. If you're using this in a Docker container consider starting it in interactive mode.", e);
		}
	}
}
