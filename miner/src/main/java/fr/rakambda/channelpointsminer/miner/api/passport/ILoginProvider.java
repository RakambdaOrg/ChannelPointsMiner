package fr.rakambda.channelpointsminer.miner.api.passport;

import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import org.jspecify.annotations.NonNull;
import java.io.IOException;

public interface ILoginProvider{
	/**
	 * Attempts a login towards Twitch. If a previous authentication file exists, it'll be restored. Else a login will be performed.
	 *
	 * @return {@link TwitchLogin}.
	 *
	 * @throws IOException    Authentication file errors.
	 * @throws LoginException Login request failed.
	 */
	@NonNull
	TwitchLogin login() throws LoginException, IOException;
}
