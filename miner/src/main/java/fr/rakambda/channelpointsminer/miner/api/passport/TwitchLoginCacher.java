package fr.rakambda.channelpointsminer.miner.api.passport;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@RequiredArgsConstructor
public class TwitchLoginCacher{
	private final Path userAuthenticationFile;
	
	/**
	 * Restore authentication from a file.
	 *
	 * @return {@link TwitchLogin} if authentication was restored, empty otherwise.
	 *
	 * @throws IOException Failed to read authentication file.
	 */
	@NonNull
	public Optional<TwitchLogin> restoreAuthentication() throws IOException{
		if(!Files.exists(userAuthenticationFile)){
			return Optional.empty();
		}
		
		var twitchLogin = JacksonUtils.read(Files.newInputStream(userAuthenticationFile), new TypeReference<TwitchLogin>(){});
		return Optional.of(twitchLogin);
	}
	
	/**
	 * Save authentication received from response into a file.
	 *
	 * @param twitchLogin Authentication to save.
	 *
	 * @throws IOException File failed to write.
	 */
	public void saveAuthentication(@NonNull TwitchLogin twitchLogin) throws IOException{
		Files.createDirectories(userAuthenticationFile.getParent());
		JacksonUtils.write(Files.newOutputStream(userAuthenticationFile, CREATE, TRUNCATE_EXISTING), twitchLogin);
	}
}
