package fr.rakambda.channelpointsminer.miner.config.login;

import org.jspecify.annotations.NonNull;

public interface IPassportApiLoginProvider extends ISavedLoginProvider{
	@NonNull
	String getPassword();
	
	boolean isUse2Fa();
}
