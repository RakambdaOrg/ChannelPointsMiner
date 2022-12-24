package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IStreamerEvent extends IEvent{
	@NotNull
	Optional<Streamer> getStreamer();
	
	@NotNull
	String getStreamerId();
	
	@NotNull
	Optional<String> getStreamerUsername();
}
