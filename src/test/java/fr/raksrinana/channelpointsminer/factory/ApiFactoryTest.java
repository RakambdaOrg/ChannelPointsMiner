package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	
	@Test
	void getGqlApi(){
		assertThat(ApiFactory.createGqlApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getHelixApi(){
		assertThat(ApiFactory.createHelixApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getKrakenApi(){
		assertThat(ApiFactory.createKrakenApi(twitchLogin)).isNotNull();
	}
	
	@Test
	void getTwitchApi(){
		assertThat(ApiFactory.createTwitchApi()).isNotNull();
	}
}