package fr.rakambda.channelpointsminer.miner.prediction.bet;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import org.jspecify.annotations.NonNull;

public class BetUtils{
	public static float getKellyValue(@NonNull Outcome chosenOutcome, @NonNull Outcome otherOutcome){
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
