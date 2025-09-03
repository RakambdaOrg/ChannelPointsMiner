package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class ChatMessageEvent implements IEvent{
	@NonNull
	private final Instant instant;
	
	@NonNull
	private final String streamer;
	@NonNull
	private final String actor;
	@NonNull
	private final String message;
	@NonNull
	private final String badges;
	
	@Setter
	private IMiner miner;
}
