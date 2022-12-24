package fr.rakambda.channelpointsminer.miner.api.passport;

import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import kong.unirest.core.Cookie;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ParallelizableTest
class TwitchLoginCacherTest{
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String USER_ID = "user-id";
	
	@TempDir
	private Path authFolder;
	
	private TwitchLoginCacher tested;
	
	private Path authFile;
	
	@BeforeEach
	void setUp() throws IOException{
		authFile = authFolder.resolve("test.json");
		
		tested = new TwitchLoginCacher(authFile);
	}
	
	@Test
	public void valueIsWritten() throws IOException{
		var login = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		tested.saveAuthentication(login);
		
		assertThat(authFile).exists().isNotEmptyFile();
		JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
	}
	
	@Test
	void restoreAuth() throws IOException{
		TestUtils.copyFromResources("api/passport/expectedAuth.json", authFile);
		
		var expected = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		assertThat(tested.restoreAuthentication()).isPresent().get()
				.usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	void restoreAuthWithUserId() throws IOException{
		TestUtils.copyFromResources("api/passport/expectedAuthWithClientId.json", authFile);
		
		var expected = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.userId(USER_ID)
				.build();
		
		assertThat(tested.restoreAuthentication()).isPresent().get()
				.usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	void restoreAuthBadFile(){
		TestUtils.copyFromResources("api/passport/badAuthFile.json", authFile);
		
		assertThrows(IOException.class, () -> tested.restoreAuthentication());
		assertThat(authFile).exists();
	}
	
	@Test
	void restoreNoFile() throws IOException{
		assertThat(tested.restoreAuthentication()).isEmpty();
	}
}