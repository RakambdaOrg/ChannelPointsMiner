package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.raksrinana.channelpointsminer.miner.event.IStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Log4j2
public class DatabaseEventHandler extends EventHandlerAdapter{
	
	private final static Pattern CHAT_PREDICTION_BADGE_PATTERN = Pattern.compile("predictions/([^,]*)");
	
	@NotNull
	private IDatabase database;
	
	@Override
	public void onEventCreatedEvent(@NotNull EventCreatedEvent event) throws Exception{
		database.deleteUnresolvedUserPredictionsForChannel(event.getStreamerUsername().orElseThrow());
	}
	
	@Override
	public void onEventUpdatedEvent(@NotNull EventUpdatedEvent event) throws Exception{
		var streamerUsername = event.getStreamerUsername();
		var predictionEvent = event.getEvent();
		
		if(predictionEvent.getStatus() == EventStatus.ACTIVE){
			log.debug("Prediction-Update: Event ACTIVE. Streamer: {}, Title: {}", streamerUsername, predictionEvent.getTitle());
			for(var outcome : predictionEvent.getOutcomes()){
				var badge = outcome.getBadge().getVersion();
				for(var predictor : outcome.getTopPredictors()){
					database.addUserPrediction(predictor.getUserDisplayName(), streamerUsername, badge);
				}
			}
		}
		else if(predictionEvent.getStatus() == EventStatus.CANCELED){
			log.info("Prediction-Update: Event CANCELED. Streamer: {}, Title: {}", streamerUsername, predictionEvent.getTitle());
			
			var ended = Optional.ofNullable(predictionEvent.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
			database.cancelPrediction(predictionEvent.getId(), predictionEvent.getChannelId(), predictionEvent.getTitle(), predictionEvent.getCreatedAt().toInstant(), ended);
		}
		else if(predictionEvent.getStatus() == EventStatus.RESOLVED){
			var winningOutcomeId = predictionEvent.getWinningOutcomeId();
			var winningOutcome = predictionEvent.getOutcomes().stream()
					.filter(e -> e.getId().equals(winningOutcomeId))
					.findAny().orElseThrow();
			var winningOutcomeBadge = winningOutcome.getBadge().getVersion();
			
			log.info("Prediction-Update: Event RESOLVED. Streamer: {}, Title: {}, Outcome: {}", streamerUsername, predictionEvent.getTitle(), winningOutcome.getTitle());
			
			var ended = Optional.ofNullable(predictionEvent.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
			var totalPoints = predictionEvent.getOutcomes().stream().mapToDouble(Outcome::getTotalPoints).sum();
			var returnRatio = totalPoints / winningOutcome.getTotalPoints();
			database.resolvePrediction(predictionEvent.getId(), predictionEvent.getChannelId(), predictionEvent.getTitle(), predictionEvent.getCreatedAt().toInstant(), ended, winningOutcome.getTitle(), winningOutcomeBadge, returnRatio);
		}
	}
	
	@Override
	public void onPointsEarnedEvent(@NotNull PointsEarnedEvent event) throws SQLException{
		var pointsEarnedData = event.getPointsEarnedData();
		var reasonCode = pointsEarnedData.getPointGain().getReasonCode();
		updateBalance(event, pointsEarnedData.getBalance(), reasonCode.name());
	}
	
	@Override
	public void onPointsSpentEvent(@NotNull PointsSpentEvent event) throws SQLException{
		updateBalance(event, event.getPointsSpentData().getBalance(), null);
	}
	
	@Override
	public void onPredictionMadeEvent(@NotNull PredictionMadeEvent event) throws SQLException{
		var placedPrediction = event.getPlacedPrediction();
		addPrediction(event, placedPrediction.getEventId(), "PREDICTED", Integer.toString(placedPrediction.getAmount()));
		var outcomeId = event.getPlacedPrediction().getOutcomeId();
		if(event.getPlacedPrediction().getBettingPrediction() != null && event.getStreamerUsername().isPresent()){
			var placedOutcome = event.getPlacedPrediction().getBettingPrediction()
					.getEvent().getOutcomes().stream().filter(o -> o.getId().equals(outcomeId)).findFirst();
			if(placedOutcome.isPresent()){
				database.addUserPrediction(event.getMiner().getUsername(), event.getStreamerUsername().get(), placedOutcome.get().getBadge().getVersion());
			}
		}
	}
	
	@Override
	public void onPredictionResultEvent(@NotNull PredictionResultEvent event) throws Exception{
		addPrediction(event, event.getPredictionResultData().getPrediction().getEventId(), "RESULT", event.getGain());
	}
	
	@Override
	public void onStreamDownEvent(@NotNull StreamDownEvent event) throws SQLException{
		updateStreamer(event);
	}
	
	@Override
	public void onStreamerAddedEvent(@NotNull StreamerAddedEvent event) throws SQLException{
		database.createChannel(event.getStreamerId(), event.getStreamerUsername().orElseThrow(() -> new IllegalStateException("No username present in streamer")));
	}
	
	@Override
	public void onStreamUpEvent(@NotNull StreamUpEvent event) throws SQLException{
		updateStreamer(event);
	}
	
	@Override
	public void onChatMessageEvent(@NotNull ChatMessageEvent event) throws Exception{
		var matcher = CHAT_PREDICTION_BADGE_PATTERN.matcher(event.getBadges());
		if(matcher.find()){
			try{
				var predictionBadge = matcher.group(1);
				log.debug("Read user prediction from chat. User: {}, Badge: {}", event.getActor(), predictionBadge);
				database.addUserPrediction(event.getActor(), event.getStreamer(), predictionBadge);
			}
			catch(SQLException e){
				log.error("SQL Exception while adding user prediction: {}", e.getMessage());
			}
		}
	}
	
	private void updateStreamer(@NotNull IStreamerEvent event) throws SQLException{
		database.updateChannelStatusTime(event.getStreamerId(), event.getInstant());
	}
	
	private void addPrediction(@NotNull IStreamerEvent event, @NotNull String eventId, @NotNull String type, @NotNull String description) throws SQLException{
		database.addPrediction(event.getStreamerId(), eventId, type, description, event.getInstant());
	}
	
	private void updateBalance(@NotNull IStreamerEvent event, @NotNull Balance balance, @Nullable String reason) throws SQLException{
		database.addBalance(event.getStreamerId(), balance.getBalance(), reason, event.getInstant());
	}
	
	@Override
	public void close(){
		database.close();
	}
}
