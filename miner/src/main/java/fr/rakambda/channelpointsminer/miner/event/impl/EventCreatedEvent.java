package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class EventCreatedEvent extends AbstractLoggableStreamerEvent{
	private final Event event;
	
	public EventCreatedEvent(@NotNull Streamer streamer, @NotNull Event event){
		super(streamer, event.getCreatedAt().toInstant());
		this.event = event;
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Prediction created [{prediction_name}]";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Prediction created [{prediction_name}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.PREDICTION_NAME.equals(key)){
			return event.getTitle();
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Title", EventVariableKey.PREDICTION_NAME);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "📑";
	}
}
