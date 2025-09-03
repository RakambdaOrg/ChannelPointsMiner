package fr.rakambda.channelpointsminer.miner.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.jspecify.annotations.NonNull;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CommonUtils{
	private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	@SneakyThrows
	public static void randomSleep(long delay, long delta){
		long actualDelay = delay - delta / 2 + ThreadLocalRandom.current().nextLong(delta);
		SleepHandler.sleep(actualDelay);
	}
	
	/**
	 * Get a user input.
	 *
	 * @param message The message to be displayed before asking input.
	 *
	 * @return User input.
	 */
	@NonNull
	public static String getUserInput(@NonNull String message){
		try{
			System.out.println(message);
			
			var scanner = new Scanner(System.in);
			return scanner.nextLine();
		}
		catch(NoSuchElementException e){
			throw new NoSuchElementException("No line was read from input. If you're using this in a Docker container consider starting it in interactive mode.", e);
		}
	}
	
	@NonNull
	public static String randomHex(int count) {
		return RandomStringUtils.secure().next(count, HEX_CHARS);
	}
	
	@NonNull
	public static String randomAlphanumeric(int count) {
		return RandomStringUtils.secure().nextAlphanumeric(count);
	}
}
