package fr.raksrinana.channelpointsminer.miner.prediction.bet;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import org.jetbrains.annotations.NotNull;

public class BetUtils{
	public static float getKellyValue(@NotNull Outcome chosenOutcome, @NotNull Outcome otherOutcome){
		if(chosenOutcome.getTotalUsers() == 0 && otherOutcome.getTotalUsers() == 0){
			return 0;
		}
		
		var firstValue = (float) chosenOutcome.getTotalUsers();
		var secondValue = (float) otherOutcome.getTotalUsers();
		
		var winProbability = firstValue / (firstValue + secondValue);
		var lossProbability = 1 - winProbability;
		var proportionGain = (chosenOutcome.getTotalPoints() + otherOutcome.getTotalPoints()) / ((float) chosenOutcome.getTotalPoints()) - 1;
		
		if(Math.abs(proportionGain) < 0.0000001){
			return 0;
		}
		
		return winProbability - lossProbability / proportionGain;
	}
}
