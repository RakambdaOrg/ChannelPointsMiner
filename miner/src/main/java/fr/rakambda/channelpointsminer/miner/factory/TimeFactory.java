package fr.rakambda.channelpointsminer.miner.factory;

import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.ZonedDateTime;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TimeFactory{
	public static Instant now(){
		return Instant.now();
	}
	
	public static ZonedDateTime nowZoned(){
		return ZonedDateTime.now();
	}
}
