package fr.rakambda.channelpointsminer.miner.prediction.bet;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.makeprediction.MakePredictionData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MakePredictionError;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MakePredictionPayload;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.EventStatus;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PredictionState;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.NotEnoughUsersBetPlacementException;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Log4j2
public class BetPlacer{
	private static final int MINIMUM_BET_AMOUNT = 10;
	
	@NotNull
	private final IMiner miner;
	
	public void placeBet(@NotNull BettingPrediction bettingPrediction){
		var event = bettingPrediction.getEvent();
		try(var ignored = LogContext.with(miner).withEventId(event.getId())){
			if(event.getStatus() != EventStatus.ACTIVE){
				log.warn("Cannot place bet anymore, current status is {}", event.getStatus());
				bettingPrediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			var outcomePicker = bettingPrediction.getStreamer().getSettings().getPredictions().getOutcomePicker();
			var outcome = outcomePicker.chooseOutcome(bettingPrediction, miner.getDatabase());
			
			var amountCalculator = bettingPrediction.getStreamer().getSettings().getPredictions().getAmountCalculator();
			var amount = amountCalculator.calculateAmount(bettingPrediction, outcome);
			if(amount < MINIMUM_BET_AMOUNT){
				log.warn("Cannot place a bet with less than {} points (was {})", MINIMUM_BET_AMOUNT, amount);
				bettingPrediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			var placement = Placement.builder()
					.bettingPrediction(bettingPrediction)
					.outcome(outcome)
					.amount(amount)
					.build();
			
			var actions = bettingPrediction.getStreamer().getSettings().getPredictions().getActions();
			for(var action : actions){
				action.perform(placement);
			}
			
			log.info("Placing bet of {} points on {} ({})", amount, outcome.getColor(), outcome.getTitle());
			var transactionId = CommonUtils.randomHex(32);
			var result = miner.getGqlApi().makePrediction(placement.getBettingPrediction().getEvent().getId(), placement.getOutcome().getId(), placement.getAmount(), transactionId);
			if(result.isEmpty()){
				log.error("Failed to place bet");
				bettingPrediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			result.map(GQLResponse::getData)
					.map(MakePredictionData::getMakePrediction)
					.map(MakePredictionPayload::getError)
					.map(MakePredictionError::getCode)
					.ifPresent(code -> {
						log.error("Failed to place bet: {}", code);
						bettingPrediction.setState(PredictionState.BET_ERROR);
					});
		}
		catch(BetPlacementException e){
			if(e instanceof NotEnoughUsersBetPlacementException){
				log.warn("Failed to place bet", e);
			}
			else{
				log.error("Failed to place bet", e);
			}
			bettingPrediction.setState(PredictionState.BET_ERROR);
		}
	}
}
