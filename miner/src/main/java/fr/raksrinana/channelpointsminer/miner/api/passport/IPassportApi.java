package fr.raksrinana.channelpointsminer.miner.api.passport;

import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public interface IPassportApi{
	/**
	 * Attempts a login towards Twitch. If a previous authentication file exists, it'll be restored. Else a login will be performed.
	 *
	 * @return {@link TwitchLogin}.
	 *
	 * @throws IOException    Authentication file errors.
	 * @throws LoginException Login request failed.
	 */
	@NotNull
	TwitchLogin login() throws LoginException, IOException;
}
