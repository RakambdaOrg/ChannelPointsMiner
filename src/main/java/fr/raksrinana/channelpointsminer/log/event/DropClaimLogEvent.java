package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.gql.data.types.DropCampaign;
import fr.raksrinana.channelpointsminer.api.gql.data.types.Game;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropClaimLogEvent extends AbstractLogEvent{
	private static final String UNKNOWN_GAME = "UnknownGame";
	
	private final TimeBasedDrop drop;
	
	public DropClaimLogEvent(@NotNull IMiner miner, @NotNull TimeBasedDrop drop){
		super(miner);
		this.drop = drop;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Claiming drop [%s | %s]".formatted(drop.getName(), getGameName().orElse(UNKNOWN_GAME));
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
	
	@NotNull
	private Optional<String> getGameName(){
		return Optional.ofNullable(drop.getCampaign())
				.map(DropCampaign::getGame)
				.map(Game::getName);
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Name").value(drop.getName()).build(),
				Field.builder().name("Game").value(getGameName().orElse(UNKNOWN_GAME)).build());
	}
}
