package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PointsSpentEvent extends AbstractLoggableStreamerEvent{
	@Getter
	private final PointsSpentData pointsSpentData;
	
	public PointsSpentEvent(@NonNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NonNull PointsSpentData pointsSpentData){
		super(streamerId, streamerUsername, streamer, pointsSpentData.getTimestamp().toInstant());
		this.pointsSpentData = pointsSpentData;
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Points spent [{balance}]";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Points spent [{balance}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.BALANCE.equals(key)){
			return millify(pointsSpentData.getBalance().getBalance(), false);
		}
		return super.lookup(key);
	}
	
	@Override
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Balance", EventVariableKey.BALANCE);
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_POINTS_LOST;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "💸";
	}
}
