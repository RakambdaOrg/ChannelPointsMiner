package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	
	@Test
	void getGqlApi(){
		assertThat(ApiFactory.getGqlApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getHelixApi(){
		assertThat(ApiFactory.getHelixApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getKrakenApi(){
		assertThat(ApiFactory.getKrakenApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getTwitchApi(){
		assertThat(ApiFactory.getTwitchApi()).isNotNull();
	}
}