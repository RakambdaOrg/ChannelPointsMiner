package fr.raksrinana.twitchminer.streamer;

import fr.raksrinana.twitchminer.prediction.FromStartDelay;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PredictionSettingsTest{
	@Test
	void copy(){
		var tested = PredictionSettings.builder()
				.delay(FromStartDelay.builder().seconds(50).build())
				.minimumPointsRequired(25)
				.build();
		
		var copy = new PredictionSettings(tested);
		
		assertThat(copy.getMinimumPointsRequired()).isEqualTo(tested.getMinimumPointsRequired());
		assertThat(copy.getDelay()).isSameAs(tested.getDelay());
	}
}