package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class ChatMessageEvent implements IEvent{
	@NotNull
	private final Instant instant;
	
	@NotNull
	private final String streamer;
	@NotNull
	private final String actor;
	@NotNull
	private final String message;
	@NotNull
	private final String badges;
	
	@Setter
	private IMiner miner;
}
