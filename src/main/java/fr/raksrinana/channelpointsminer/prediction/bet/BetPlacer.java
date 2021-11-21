package fr.raksrinana.channelpointsminer.prediction.bet;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionError;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.factory.TransactionIdFactory;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
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
			var outcome = outcomePicker.chooseOutcome(bettingPrediction);
			
			var amountCalculator = bettingPrediction.getStreamer().getSettings().getPredictions().getAmountCalculator();
			var amount = amountCalculator.calculateAmount(bettingPrediction, outcome);
			if(amount <= 10){
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
			var transactionId = TransactionIdFactory.create();
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
			log.error("Failed to place bet", e);
			bettingPrediction.setState(PredictionState.BET_ERROR);
		}
	}
}
