package fr.rakambda.channelpointsminer.miner.config.login;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public interface IPassportApiLoginProvider{
	@NotNull
	TwitchClient getTwitchClient();
	
	@NotNull
	String getPassword();
	
	boolean isUse2Fa();
	
	@NotNull
	Path getAuthenticationFolder();
}
