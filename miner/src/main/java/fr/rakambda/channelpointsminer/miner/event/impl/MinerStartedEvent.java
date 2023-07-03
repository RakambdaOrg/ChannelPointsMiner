package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MinerStartedEvent extends AbstractLoggableEvent{
	private final String version;
	private final String commit;
	private final String branch;
	
	public MinerStartedEvent(@NotNull String version, @NotNull String commit, @NotNull String branch, @NotNull Instant instant){
		super(instant);
		this.version = version;
		this.commit = commit;
		this.branch = branch;
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Miner started (version: {version} [{commit} - {branch}])";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : Miner started with version {version} [{commit} - {branch}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.VERSION.equals(key)){
			return version;
		}
		if(EventVariableKey.COMMIT.equals(key)){
			return commit;
		}
		if(EventVariableKey.BRANCH.equals(key)){
			return branch;
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of(
				"Version", EventVariableKey.VERSION,
				"Commit", EventVariableKey.COMMIT,
				"Branch", EventVariableKey.BRANCH
		);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "✅";
	}
}
