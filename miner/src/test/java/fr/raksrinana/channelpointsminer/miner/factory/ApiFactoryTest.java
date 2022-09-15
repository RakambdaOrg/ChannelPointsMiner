package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URL;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
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
	void createTwitchApi(){
		assertThat(ApiFactory.createTwitchApi(twitchLogin)).isNotNull().isInstanceOf(TwitchApi.class);
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