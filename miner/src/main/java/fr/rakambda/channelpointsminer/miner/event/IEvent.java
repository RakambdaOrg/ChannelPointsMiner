package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

public interface IEvent{
	@NotNull
	Instant getInstant();
	
	@NotNull
	IMiner getMiner();
}
