package fr.raksrinana.channelpointsminer.prediction.bet;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionError;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.factory.TransactionIdFactory;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
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
	
	public void placeBet(@NotNull Prediction prediction){
		var event = prediction.getEvent();
		try(var ignored = LogContext.empty().withEventId(event.getId())){
			if(event.getStatus() != EventStatus.ACTIVE){
				log.warn("Cannot place bet anymore, current status is {}", event.getStatus());
				prediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			var outcomePicker = prediction.getStreamer().getSettings().getPredictions().getOutcomePicker();
			var outcome = outcomePicker.chooseOutcome(prediction);
			
			var amountCalculator = prediction.getStreamer().getSettings().getPredictions().getAmountCalculator();
			var amount = amountCalculator.calculateAmount(prediction, outcome);
			if(amount <= 10){
				log.warn("Cannot place a bet with less than {} points (was {})", MINIMUM_BET_AMOUNT, amount);
				prediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			log.info("Placing bet of {} points on {} ({})", amount, outcome.getColor(), outcome.getTitle());
			var result = miner.getGqlApi().makePrediction(event.getId(), outcome.getId(), amount, TransactionIdFactory.create());
			if(result.isEmpty()){
				log.error("Failed to place bet");
				prediction.setState(PredictionState.BET_ERROR);
				return;
			}
			
			result.map(GQLResponse::getData)
					.map(MakePredictionData::getMakePrediction)
					.map(MakePredictionPayload::getError)
					.map(MakePredictionError::getCode)
					.ifPresent(code -> {
						log.error("Failed to place bet: {}", code);
						prediction.setState(PredictionState.BET_ERROR);
					});
		}
		catch(fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException e){
			log.error("Failed to place bet", e);
			prediction.setState(PredictionState.BET_ERROR);
		}
	}
}
