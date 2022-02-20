package fr.raksrinana.channelpointsminer.event;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

public interface IEvent{
	@NotNull
	IMiner getMiner();
	
	@NotNull
	Instant getInstant();
}
