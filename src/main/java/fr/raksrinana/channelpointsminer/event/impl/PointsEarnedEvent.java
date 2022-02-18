package fr.raksrinana.channelpointsminer.event.impl;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.event.AbstractStreamerEvent;
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
public class PointsEarnedEvent extends AbstractStreamerEvent{
	private final PointsEarnedData pointsEarnedData;
	
	public PointsEarnedEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull PointsEarnedData pointsEarnedData){
		super(miner, streamerId, streamerUsername, streamer);
		this.pointsEarnedData = pointsEarnedData;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Points earned [%s | %s | %s]".formatted(
				millify(pointsEarnedData.getPointGain().getTotalPoints(), true),
				pointsEarnedData.getPointGain().getReasonCode(),
				millify(pointsEarnedData.getBalance().getBalance(), false));
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "💰";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_POINTS_WON;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Points earned";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Points").value(millify(pointsEarnedData.getPointGain().getTotalPoints(), true)).build(),
				Field.builder().name("Reason").value(pointsEarnedData.getPointGain().getReasonCode().toString()).build(),
				Field.builder().name("Balance").value(millify(pointsEarnedData.getBalance().getBalance(), false)).build());
	}
}
