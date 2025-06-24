package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PointsEarnedEvent extends AbstractLoggableStreamerEvent{
	@Getter
	private final PointsEarnedData pointsEarnedData;
	
	public PointsEarnedEvent(@NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull PointsEarnedData pointsEarnedData){
		super(streamerId, streamerUsername, streamer, pointsEarnedData.getTimestamp().toInstant());
		this.pointsEarnedData = pointsEarnedData;
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Points earned [{points} | {reason} | {balance}]";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Points earned [{points} | {reason} | {balance}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.POINTS.equals(key)){
			return millify(pointsEarnedData.getPointGain().getTotalPoints(), true);
		}
		if(EventVariableKey.REASON.equals(key)){
			return pointsEarnedData.getPointGain().getReasonCode();
		}
		if(EventVariableKey.BALANCE.equals(key)){
			return millify(pointsEarnedData.getBalance().getBalance(), false);
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of(
				"Points", EventVariableKey.POINTS,
				"Reason", EventVariableKey.REASON,
				"Balance", EventVariableKey.BALANCE
		);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_POINTS_WON;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "💰";
	}
}
