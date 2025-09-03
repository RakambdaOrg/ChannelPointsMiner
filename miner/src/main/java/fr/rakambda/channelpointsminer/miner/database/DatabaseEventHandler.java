package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.EventStatus;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Log4j2
public class DatabaseEventHandler extends EventHandlerAdapter{
	
	private static final Pattern CHAT_PREDICTION_BADGE_PATTERN = Pattern.compile("predictions/([^,]*)");
	private static final double INFINITE_RETURN_RATIO = 100_000D;
	
	@NonNull
	private final IDatabase database;
	private final boolean recordUserPredictions;
	
	@Override
	public void onEventCreatedEvent(@NonNull EventCreatedEvent event) throws Exception{
		database.deleteUserPredictionsForChannel(event.getStreamerUsername().orElseThrow());
	}
	
	@Override
	public void onEventUpdatedEvent(@NonNull EventUpdatedEvent event) throws Exception{
		var streamerUsername = event.getStreamerUsername();
		var predictionEvent = event.getEvent();
		
		if(predictionEvent.getStatus() == EventStatus.ACTIVE){
			log.debug("Prediction-Update: Event ACTIVE. Streamer: {}, Title: {}", streamerUsername, predictionEvent.getTitle());
			if(recordUserPredictions){
				for(var outcome : predictionEvent.getOutcomes()){
					var badge = outcome.getBadge().getVersion();
					for(var predictor : outcome.getTopPredictors()){
						database.addUserPrediction(predictor.getUserDisplayName(), event.getEvent().getChannelId(), badge);
					}
				}
			}
		}
		else if(predictionEvent.getStatus() == EventStatus.CANCELED){
			log.info("Prediction-Update: Event CANCELED. Streamer: {}, Title: {}", streamerUsername, predictionEvent.getTitle());
			database.cancelPrediction(predictionEvent);
		}
		else if(predictionEvent.getStatus() == EventStatus.RESOLVED){
			var winningOutcomeId = predictionEvent.getWinningOutcomeId();
			var winningOutcome = predictionEvent.getOutcomes().stream()
					.filter(e -> e.getId().equals(winningOutcomeId))
					.findAny().orElseThrow();
			var winningOutcomeBadge = winningOutcome.getBadge().getVersion();
			
			log.info("Prediction-Update: Event RESOLVED. Streamer: {}, Title: {}, Outcome: {}", streamerUsername, predictionEvent.getTitle(), winningOutcome.getTitle());
			
			var totalPoints = predictionEvent.getOutcomes().stream().mapToDouble(Outcome::getTotalPoints).sum();
			var returnRatio = winningOutcome.getTotalPoints() == 0L ? INFINITE_RETURN_RATIO : totalPoints / winningOutcome.getTotalPoints();
			database.resolvePrediction(predictionEvent, winningOutcome.getTitle(), winningOutcomeBadge, returnRatio);
		}
	}
	
	@Override
	public void onPointsEarnedEvent(@NonNull PointsEarnedEvent event) throws SQLException{
		var pointsEarnedData = event.getPointsEarnedData();
		var reasonCode = pointsEarnedData.getPointGain().getReasonCode();
		updateBalance(event, pointsEarnedData.getBalance(), reasonCode);
	}
	
	@Override
	public void onPointsSpentEvent(@NonNull PointsSpentEvent event) throws SQLException{
		updateBalance(event, event.getPointsSpentData().getBalance(), null);
	}
	
	@Override
	public void onPredictionMadeEvent(@NonNull PredictionMadeEvent event) throws SQLException{
		var placedPrediction = event.getPlacedPrediction();
		addPrediction(event, placedPrediction.getEventId(), "PREDICTED", Integer.toString(placedPrediction.getAmount()));
		
		if(placedPrediction.getBettingPrediction() != null && event.getStreamerUsername().isPresent()){
			var outcomeId = placedPrediction.getOutcomeId();
			var placedOutcome = placedPrediction.getBettingPrediction().getEvent().getOutcomes().stream()
					.filter(o -> o.getId().equals(outcomeId))
					.findFirst();
			if(placedOutcome.isPresent()){
				database.addUserPrediction(event.getMiner().getUsername(), event.getStreamerId(), placedOutcome.get().getBadge().getVersion());
			}
		}
	}
	
	@Override
	public void onPredictionResultEvent(@NonNull PredictionResultEvent event) throws Exception{
		addPrediction(event, event.getPredictionResultData().getPrediction().getEventId(), "RESULT", event.getGain());
	}
	
	@Override
	public void onStreamDownEvent(@NonNull StreamDownEvent event) throws SQLException{
		updateStreamer(event);
	}
	
	@Override
	public void onStreamerAddedEvent(@NonNull StreamerAddedEvent event) throws SQLException{
		database.createChannel(event.getStreamerId(), event.getStreamerUsername().orElseThrow(() -> new IllegalStateException("No username present in streamer")));
	}
	
	@Override
	public void onStreamUpEvent(@NonNull StreamUpEvent event) throws SQLException{
		updateStreamer(event);
	}
	
	@Override
	public void onChatMessageEvent(@NonNull ChatMessageEvent event){
		var matcher = CHAT_PREDICTION_BADGE_PATTERN.matcher(event.getBadges());
		if(matcher.find()){
			try{
				var predictionBadge = matcher.group(1);
				log.debug("Read user prediction from chat. User: {}, Badge: {}", event.getActor(), predictionBadge);
				
				var streamerId = database.getStreamerIdFromName(event.getStreamer());
				if(streamerId.isEmpty()){
					log.warn("Failed to get streamer id from name: {}", event.getStreamer());
					return;
				}
				database.addUserPrediction(event.getActor(), streamerId.get(), predictionBadge);
			}
			catch(SQLException e){
				log.error("SQL Exception while adding user prediction", e);
			}
		}
	}
	
	private void updateStreamer(@NonNull IStreamerEvent event) throws SQLException{
		database.updateChannelStatusTime(event.getStreamerId(), event.getInstant());
	}
	
	private void addPrediction(@NonNull IStreamerEvent event, @NonNull String eventId, @NonNull String type, @NonNull String description) throws SQLException{
		database.addPrediction(event.getStreamerId(), eventId, type, description, event.getInstant());
	}
	
	private void updateBalance(@NonNull IStreamerEvent event, @NonNull Balance balance, @Nullable String reason) throws SQLException{
		database.addBalance(event.getStreamerId(), balance.getBalance(), reason, event.getInstant());
	}
	
	@Override
	public void close() throws IOException{
		database.close();
	}
}
