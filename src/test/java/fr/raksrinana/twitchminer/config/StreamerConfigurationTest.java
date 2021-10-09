package fr.raksrinana.twitchminer.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class StreamerConfigurationTest{
	@ParameterizedTest
	@ValueSource(strings = {
			"username",
			"USERNAME",
			"Username",
			"UsErNaMe"
	})
	void equals(String username){
		var actual = StreamerConfiguration.builder().username(username).build();
		var expected = StreamerConfiguration.builder().username("username").build();
		
		assertThat(actual).isEqualTo(expected);
	}
}