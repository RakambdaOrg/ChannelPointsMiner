package fr.raksrinana.twitchminer.prediction;

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
class FromEndDelayTest{
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	private static final int SECONDS = 30;
	private static final int WINDOW_SECONDS = 300;
	
	private final FromEndDelay tesed = FromEndDelay.builder().seconds(SECONDS).build();
	
	@Mock
	private Event event;
	
	@Test
	void calculate(){
		when(event.getCreatedAt()).thenReturn(EVENT_DATE);
		when(event.getPredictionWindowSeconds()).thenReturn(WINDOW_SECONDS);
		
		assertThat(tesed.calculate(event)).isEqualTo(ZonedDateTime.of(2021, 10, 10, 12, 4, 30, 0, UTC));
	}
}