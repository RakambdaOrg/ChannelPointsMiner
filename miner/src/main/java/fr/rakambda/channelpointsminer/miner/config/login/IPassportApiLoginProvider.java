package fr.rakambda.channelpointsminer.miner.config.login;

import org.jetbrains.annotations.NotNull;

public interface IPassportApiLoginProvider extends ISavedLoginProvider{
	@NotNull
	String getPassword();
	
	boolean isUse2Fa();
}
