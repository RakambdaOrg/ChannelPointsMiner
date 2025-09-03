package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BetPlacerFactory{
	public static BetPlacer created(@NonNull IMiner miner){
		return new BetPlacer(miner);
	}
}
