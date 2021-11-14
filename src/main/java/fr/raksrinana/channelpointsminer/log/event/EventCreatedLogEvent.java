package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class EventCreatedLogEvent extends AbstractLogEvent{
	private final Event event;
	
	public EventCreatedLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer, @NotNull Event event){
		super(miner, streamer);
		this.event = event;
	}
	
	@Override
	public String getAsLog(){
		return "Prediction created [%s]".formatted(event.getTitle());
	}
	
	@Override
	protected String getEmoji(){
		return "📑";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	protected String getEmbedDescription(){
		return "Prediction created";
	}
	
	@Override
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(Field.builder()
				.name("Title")
				.value(event.getTitle())
				.build());
	}
}
