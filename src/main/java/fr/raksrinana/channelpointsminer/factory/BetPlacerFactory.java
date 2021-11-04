package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BetPlacerFactory{
	public static BetPlacer created(@NotNull IMiner miner){
		return new BetPlacer(miner);
	}
}
