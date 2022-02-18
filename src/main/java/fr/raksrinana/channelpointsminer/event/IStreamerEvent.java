package fr.raksrinana.channelpointsminer.event;

import fr.raksrinana.channelpointsminer.streamer.Streamer;
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
