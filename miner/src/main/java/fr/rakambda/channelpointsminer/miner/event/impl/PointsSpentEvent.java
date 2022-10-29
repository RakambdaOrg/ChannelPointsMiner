package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PointsSpentEvent extends AbstractLoggableStreamerEvent{
	@Getter
	private final PointsSpentData pointsSpentData;
	
	public PointsSpentEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull PointsSpentData pointsSpentData){
		super(miner, streamerId, streamerUsername, streamer, pointsSpentData.getTimestamp().toInstant());
		this.pointsSpentData = pointsSpentData;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Points spent [%s]".formatted(millify(pointsSpentData.getBalance().getBalance(), false));
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "💸";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_POINTS_LOST;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Points spent";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(Field.builder()
				.name("Balance")
				.value(millify(pointsSpentData.getBalance().getBalance(), false))
				.build());
	}
}
