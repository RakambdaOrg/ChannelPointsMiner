package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropClaimEvent extends AbstractLoggableEvent{
	private final TimeBasedDrop drop;
	
	public DropClaimEvent(@NotNull IMiner miner, @NotNull TimeBasedDrop drop, @NotNull Instant instant){
		super(miner, instant);
		this.drop = drop;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Drop available [%s]".formatted(drop.getName());
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
		return "Drop available";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Name").value(drop.getName()).build());
	}
}
