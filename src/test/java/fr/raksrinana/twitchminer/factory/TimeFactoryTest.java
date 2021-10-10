package fr.raksrinana.twitchminer.factory;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TimeFactoryTest{
	@Test
	void create(){
		assertThat(TimeFactory.now()).isNotNull();
	}
}