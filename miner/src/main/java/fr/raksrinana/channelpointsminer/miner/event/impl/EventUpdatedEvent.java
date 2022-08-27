package fr.raksrinana.channelpointsminer.miner.event.impl;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.event.IEvent;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class EventUpdatedEvent implements IEvent{
	@NotNull
	private final IMiner miner;
	@NotNull
	private final Instant instant;
	@NotNull
	private final String streamerUsername;
	@NotNull
	private final Event event;
}
