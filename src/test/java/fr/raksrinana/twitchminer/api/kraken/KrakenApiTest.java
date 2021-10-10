package fr.raksrinana.twitchminer.api.kraken;

import fr.raksrinana.twitchminer.TestUtils;
import fr.raksrinana.twitchminer.api.kraken.data.follows.Channel;
import fr.raksrinana.twitchminer.api.kraken.data.follows.Follow;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import static fr.raksrinana.twitchminer.TestUtils.getAllResourceContent;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KrakenApiTest{
	private static final String NOMINAL_RESOURCE = "api/kraken/nominal.json";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CLIENT_ID = "jzkbprff40iqj646a697cyrvl0zt2m6";
	
	@InjectMocks
	private KrakenApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	private MockClient unirest;
	private Follow nominalFollow;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		TestUtils.setupUnirest();
		unirest = MockClient.register();
		
		when(twitchLogin.getUserId()).thenReturn(USER_ID);
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		nominalFollow = Follow.builder()
				.createdAt(ZonedDateTime.of(2021, 7, 20, 16, 39, 43, 0, UTC))
				.notifications(true)
				.channel(Channel.builder()
						.mature(false)
						.status("Follow status")
						.broadcasterLanguage("en")
						.broadcasterSoftware("unknown_rtmp")
						.displayName("Display name")
						.game("Game")
						.language("en")
						.id("123456789")
						.name("streamername")
						.createdAt(ZonedDateTime.of(2020, 10, 20, 21, 24, 38, 0, UTC))
						.updatedAt(ZonedDateTime.of(2021, 10, 10, 7, 4, 35, 0, UTC))
						.partner(true)
						.logo(new URL("https://logo"))
						.videoBanner(new URL("https://video-banner"))
						.profileBanner(new URL("https://profile-banner"))
						.url(new URL("https://www.twitch.tv/streamername"))
						.views(123456789L)
						.followers(123456L)
						.broadcasterType("0cpm")
						.description("description")
						.privateVideo(false)
						.privacyOptionsEnabled(false)
						.build())
				.build();
	}
	
	@Test
	void getFollows(){
		int limit = 40;
		int offset = 50;
		
		unirest.expect(GET, "https://api.twitch.tv/kraken/users/%s/follows/channels".formatted(USER_ID))
				.header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.queryString("limit", String.valueOf(limit))
				.queryString("offset", String.valueOf(offset))
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.thenReturn(getAllResourceContent(NOMINAL_RESOURCE))
				.withStatus(200);
		
		assertThat(tested.getFollows(limit, offset)).containsExactlyInAnyOrder(nominalFollow);
		
		unirest.verifyAll();
	}
	
	@Test
	void getFollowsDefaultLimitAndOffset(){
		unirest.expect(GET, "https://api.twitch.tv/kraken/users/%s/follows/channels".formatted(USER_ID))
				.header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.queryString("limit", "100")
				.queryString("offset", "0")
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.thenReturn(getAllResourceContent(NOMINAL_RESOURCE))
				.withStatus(200);
		
		assertThat(tested.getFollows()).containsExactlyInAnyOrder(nominalFollow);
		
		unirest.verifyAll();
	}
	
	@Test
	void getFollowsWithSeveralPage(){
		int limit = 1;
		int offset = 0;
		
		unirest.expect(GET, "https://api.twitch.tv/kraken/users/%s/follows/channels".formatted(USER_ID))
				.header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.queryString("limit", String.valueOf(limit))
				.queryString("offset", String.valueOf(offset))
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.thenReturn(getAllResourceContent(NOMINAL_RESOURCE))
				.withStatus(200);
		
		unirest.expect(GET, "https://api.twitch.tv/kraken/users/%s/follows/channels".formatted(USER_ID))
				.header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.queryString("limit", String.valueOf(limit))
				.queryString("offset", String.valueOf(offset + 1))
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.thenReturn("""
						{
						  "_total": 0,
						  "follows": []
						}""")
				.withStatus(200);
		
		assertThat(tested.getFollows(limit, offset)).containsExactlyInAnyOrder(nominalFollow);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(){
		unirest.expect(GET, "https://api.twitch.tv/kraken/users/%s/follows/channels".formatted(USER_ID))
				.header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.twitchtv.v5+json")
				.header("Client-Id", CLIENT_ID)
				.queryString("limit", "100")
				.queryString("offset", "0")
				.queryString("direction", "asc")
				.queryString("sortby", "login")
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.getFollows()).isEmpty();
		
		unirest.verifyAll();
	}
}