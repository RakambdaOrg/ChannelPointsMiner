package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropClaimLogEvent extends AbstractLogEvent{
	private final TimeBasedDrop drop;
	
	public DropClaimLogEvent(@NotNull IMiner miner, @NotNull TimeBasedDrop drop){
		super(miner);
		this.drop = drop;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Claiming drop [%s]".formatted(drop.getName());
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🎁";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	
	@NotNull
	protected String getEmbedDescription(){
		return "Claiming drop";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Name").value(drop.getName()).build());
	}
}
