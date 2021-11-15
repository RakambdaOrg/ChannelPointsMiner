package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
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
public class PointsSpentLogEvent extends AbstractLogEvent{
	private final PointsSpentData pointsSpentData;
	
	public PointsSpentLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer, @NotNull PointsSpentData pointsSpentData){
		super(miner, streamer);
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
