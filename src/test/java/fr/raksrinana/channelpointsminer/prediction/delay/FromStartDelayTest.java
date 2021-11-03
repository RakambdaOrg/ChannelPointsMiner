package fr.raksrinana.twitchminer.prediction.delay;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FromStartDelayTest{
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	private static final int SECONDS = 30;
	
	private final FromStartDelay tesed = FromStartDelay.builder().seconds(SECONDS).build();
	
	@Mock
	private Event event;
	
	@Test
	void calculate(){
		when(event.getCreatedAt()).thenReturn(EVENT_DATE);
		
		assertThat(tesed.calculate(event)).isEqualTo(EVENT_DATE.plusSeconds(SECONDS));
	}
}