package fr.raksrinana.channelpointsminer.factory;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TimeFactoryTest{
	@Test
	void create(){
		assertThat(TimeFactory.now()).isNotNull();
	}
	
	@Test
	void createZoned(){
		assertThat(TimeFactory.nowZoned()).isNotNull();
	}
}