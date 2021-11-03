package fr.raksrinana.twitchminer.prediction.bet;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.twitchminer.api.gql.data.types.MakePredictionError;
import fr.raksrinana.twitchminer.api.gql.data.types.MakePredictionPayload;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.twitchminer.factory.TransactionIdFactory;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.log.LogContext;
import fr.raksrinana.twitchminer.miner.IMiner;
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
				return;
			}
			
			var outcomePicker = prediction.getStreamer().getSettings().getPredictions().getOutcomePicker();
			var outcome = outcomePicker.chooseOutcome(prediction);
			
			var amountCalculator = prediction.getStreamer().getSettings().getPredictions().getAmountCalculator();
			var amount = amountCalculator.calculateAmount(prediction, outcome);
			if(amount <= 10){
				log.warn("Cannot place a bet with less than {} points (was {})", MINIMUM_BET_AMOUNT, amount);
				return;
			}
			
			log.info("Placing bet of {} points on {} ({})", amount, outcome.getColor(), outcome.getTitle());
			var result = miner.getGqlApi().makePrediction(event.getId(), outcome.getId(), amount, TransactionIdFactory.create());
			if(result.isEmpty()){
				log.error("Failed to place bet");
				return;
			}
			
			result.map(GQLResponse::getData)
					.map(MakePredictionData::getMakePrediction)
					.map(MakePredictionPayload::getError)
					.map(MakePredictionError::getCode)
					.ifPresentOrElse(
							code -> log.error("Failled to place bet: {}", code),
							() -> log.info("Bet placed successfully")
					);
		}
		catch(BetPlacementException e){
			log.error("Failed to place bet", e);
		}
	}
}
