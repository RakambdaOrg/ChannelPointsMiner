package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

public interface IStreamerEvent extends IEvent{
	@NonNull
	Optional<Streamer> getStreamer();
	
	@NonNull
	String getStreamerId();
	
	@NonNull
	Optional<String> getStreamerUsername();
}
