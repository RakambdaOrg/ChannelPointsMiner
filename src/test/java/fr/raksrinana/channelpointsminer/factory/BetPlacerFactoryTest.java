package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BetPlacerFactoryTest{
	@Mock
	private IMiner miner;
	
	@Test
	void create(){
		assertThat(BetPlacerFactory.created(miner)).isNotNull().isInstanceOf(BetPlacer.class);
	}
}