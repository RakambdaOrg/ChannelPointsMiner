package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.helix.HelixApi;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private URL url;
	
	@Test
	void getGqlApi(){
		assertThat(ApiFactory.createGqlApi(twitchLogin)).isNotNull().isInstanceOf(GQLApi.class);
	}
	
	@Test
	void getHelixApi(){
		assertThat(ApiFactory.createHelixApi(twitchLogin)).isNotNull().isInstanceOf(HelixApi.class);
	}
	
	@Test
	void getKrakenApi(){
		assertThat(ApiFactory.createKrakenApi(twitchLogin)).isNotNull().isInstanceOf(KrakenApi.class);
	}
	
	@Test
	void getTwitchApi(){
		assertThat(ApiFactory.createTwitchApi()).isNotNull().isInstanceOf(TwitchApi.class);
	}
	
	@Test
	void getDiscordApi(){
		assertThat(ApiFactory.createdDiscordApi(url)).isNotNull().isInstanceOf(DiscordApi.class);
	}
}