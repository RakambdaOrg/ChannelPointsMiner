package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MinerStartedLogEvent extends AbstractLogEvent{
	private final String version;
	private final String commit;
	private final String branch;
	
	public MinerStartedLogEvent(@NotNull IMiner miner, @NotNull String version, @NotNull String commit, @NotNull String branch){
		super(miner);
		this.version = version;
		this.commit = commit;
		this.branch = branch;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Miner started (version: %s [%s - %s])".formatted(version, commit, branch);
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "✅";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Miner started";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Version").value(version).build(),
				Field.builder().name("Commit").value(commit).build(),
				Field.builder().name("Branch").value(branch).build());
	}
}
