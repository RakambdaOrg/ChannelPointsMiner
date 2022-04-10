package fr.raksrinana.channelpointsminer.miner.prediction.bet;

import org.jetbrains.annotations.NotNull;

public class BetPlacementException extends Exception{
	public BetPlacementException(@NotNull String message){
		super(message);
	}
}
