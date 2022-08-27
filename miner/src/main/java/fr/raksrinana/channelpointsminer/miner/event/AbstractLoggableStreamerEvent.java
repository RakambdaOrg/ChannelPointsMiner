package fr.raksrinana.channelpointsminer.miner.event;

import fr.raksrinana.channelpointsminer.miner.api.discord.data.Author;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractLoggableStreamerEvent extends AbstractLoggableEvent implements IStreamerEvent{
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	@Getter
	private final String streamerId;
	private final String streamerUsername;
	private final Streamer streamer;
	
	public AbstractLoggableStreamerEvent(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull Instant instant){
		this(miner, streamer.getId(), streamer.getUsername(), streamer, instant);
	}
	
	public AbstractLoggableStreamerEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull Instant instant){
		super(miner, instant);
		this.streamerId = streamerId;
		this.streamerUsername = streamerUsername;
		this.streamer = streamer;
	}
	
	@Override
	@NotNull
	protected String getWebhookContent(){
		return "[%s] %s %s : %s".formatted(
				getMiner().getUsername(),
				getEmoji(),
				getStreamerUsername().orElse(UNKNOWN_STREAMER),
				getWebhookMessage());
	}
	
	@Nullable
	@Override
	protected Author getEmbedAuthor(){
		return getStreamerUsername()
				.map(username -> Author.builder().name(username)
						.iconUrl(getStreamer().flatMap(Streamer::getProfileImage).orElse(null))
						.url(getStreamer().map(Streamer::getChannelUrl).orElse(null))
						.build()
				)
				.orElse(null);
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
