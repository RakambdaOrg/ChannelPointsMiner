package fr.rakambda.channelpointsminer.miner.prediction.bet.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BetPlacementException extends Exception{
	public BetPlacementException(@NotNull String message){
		super(message);
	}
	
	public BetPlacementException(@NotNull String message, @Nullable Throwable e){
		super(message, e);
	}
}
