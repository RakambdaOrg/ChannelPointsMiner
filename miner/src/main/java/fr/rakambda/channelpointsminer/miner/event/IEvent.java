package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import org.jspecify.annotations.NonNull;
import java.time.Instant;

public interface IEvent{
	@NonNull
	Instant getInstant();
	
	@NonNull
	IMiner getMiner();
	
	void setMiner(@NonNull IMiner miner);
}
