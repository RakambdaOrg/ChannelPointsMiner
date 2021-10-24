package fr.raksrinana.twitchminer.factory;

import lombok.NoArgsConstructor;
import java.time.Instant;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TimeFactory{
	public static Instant now(){
		return Instant.now();
	}
}
