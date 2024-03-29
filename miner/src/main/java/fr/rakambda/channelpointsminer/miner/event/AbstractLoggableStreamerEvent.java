package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractLoggableStreamerEvent extends AbstractLoggableEvent implements IStreamerEvent{
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	private final String streamerId;
	private final String streamerUsername;
	private final Streamer streamer;
	
	public AbstractLoggableStreamerEvent(@NotNull Streamer streamer, @NotNull Instant instant){
		this(streamer.getId(), streamer.getUsername(), streamer, instant);
	}
	
	public AbstractLoggableStreamerEvent(@NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull Instant instant){
		super(instant);
		this.streamerId = streamerId;
		this.streamerUsername = streamerUsername;
		this.streamer = streamer;
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.STREAMER.equals(key)){
			return getStreamerUsername().orElse(UNKNOWN_STREAMER);
		}
		if(EventVariableKey.STREAMER_URL.equals(key)){
			return getStreamer().map(Streamer::getChannelUrl).map(URL::toString).orElse(null);
		}
		if(EventVariableKey.STREAMER_PROFILE_PICTURE_URL.equals(key)){
			return getStreamer().flatMap(Streamer::getProfileImage).map(URL::toString).orElse(null);
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Optional<Streamer> getStreamer(){
		return Optional.ofNullable(streamer);
	}
	
	@Override
	@NotNull
	public Optional<String> getStreamerUsername(){
		return Optional.ofNullable(streamerUsername);
	}
}
