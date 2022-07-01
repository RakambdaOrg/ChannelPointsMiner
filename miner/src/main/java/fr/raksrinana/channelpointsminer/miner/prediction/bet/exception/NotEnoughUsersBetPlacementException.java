package fr.raksrinana.channelpointsminer.miner.prediction.bet.exception;

public class NotEnoughUsersBetPlacementException extends BetPlacementException{
	public NotEnoughUsersBetPlacementException(int count){
		super(count + " user(s) participated, can't decide which side to place the bet onto");
	}
}
