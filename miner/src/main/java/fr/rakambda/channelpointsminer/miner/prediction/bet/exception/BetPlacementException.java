package fr.rakambda.channelpointsminer.miner.prediction.bet.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BetPlacementException extends Exception{
	public BetPlacementException(@NonNull String message){
		super(message);
	}
	
	public BetPlacementException(@NonNull String message, @Nullable Throwable e){
		super(message, e);
	}
}
