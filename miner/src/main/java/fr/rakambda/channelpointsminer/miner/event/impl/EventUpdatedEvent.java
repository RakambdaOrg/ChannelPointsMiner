package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
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
public class EventUpdatedEvent implements IEvent{
	@NonNull
	private final Instant instant;
	@NonNull
	private final String streamerUsername;
	@NonNull
	private final Event event;
	
	@Setter
	private IMiner miner;
}
