package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
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
public class PointsEarnedLogEvent extends AbstractLogEvent{
	private final PointsEarnedData pointsEarnedData;
	
	public PointsEarnedLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer, @NotNull PointsEarnedData pointsEarnedData){
		super(miner, streamer);
		this.pointsEarnedData = pointsEarnedData;
	}
	
	@Override
	public String getAsLog(){
		return "Points earned [%+d | %s | %s]".formatted(
				pointsEarnedData.getPointGain().getTotalPoints(),
				pointsEarnedData.getPointGain().getReasonCode(),
				pointsEarnedData.getBalance().getBalance());
	}
	
	@Override
	protected String getEmoji(){
		return "💰";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_POINTS_WON;
	}
	
	@Override
	protected String getEmbedDescription(){
		return "Points earned";
	}
	
	@Override
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Points").value(Integer.toString(pointsEarnedData.getPointGain().getTotalPoints())).build(),
				Field.builder().name("Reason").value(pointsEarnedData.getPointGain().getReasonCode().toString()).build(),
				Field.builder().name("Balance").value(Integer.toString(pointsEarnedData.getBalance().getBalance())).build());
	}
}
