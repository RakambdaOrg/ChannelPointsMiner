package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.helix.HelixApi;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URL;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private URL url;
	
	@Test
	void createGqlApi(){
		assertThat(ApiFactory.createGqlApi(twitchLogin)).isNotNull().isInstanceOf(GQLApi.class);
	}
	
	@Test
	void createHelixApi(){
		assertThat(ApiFactory.createHelixApi(twitchLogin)).isNotNull().isInstanceOf(HelixApi.class);
	}
	
	@Test
	void createKrakenApi(){
		assertThat(ApiFactory.createKrakenApi(twitchLogin)).isNotNull().isInstanceOf(KrakenApi.class);
	}
	
	@Test
	void createTwitchApi(){
		assertThat(ApiFactory.createTwitchApi()).isNotNull().isInstanceOf(TwitchApi.class);
	}
	
	@Test
	void createDiscordApi(){
		assertThat(ApiFactory.createdDiscordApi(url)).isNotNull().isInstanceOf(DiscordApi.class);
	}
	
	@Test
	void createPassportApi(){
		assertThat(ApiFactory.createPassportApi("user", "pass", Paths.get("."), false)).isNotNull().isInstanceOf(PassportApi.class);
	}
}