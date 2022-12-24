package fr.rakambda.channelpointsminer.miner.config.login;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public interface ISavedLoginProvider{
	@NotNull
	TwitchClient getTwitchClient();
	
	@NotNull
	Path getAuthenticationFolder();
}
