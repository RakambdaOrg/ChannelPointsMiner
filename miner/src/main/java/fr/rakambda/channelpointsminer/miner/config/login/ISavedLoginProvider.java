package fr.rakambda.channelpointsminer.miner.config.login;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;

public interface ISavedLoginProvider{
	@NonNull
	TwitchClient getTwitchClient();
	
	@NonNull
	Path getAuthenticationFolder();
}
