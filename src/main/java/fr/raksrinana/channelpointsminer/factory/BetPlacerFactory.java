package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BetPlacerFactory{
	public static BetPlacer created(@NotNull IMiner miner){
		return new BetPlacer(miner);
	}
}
