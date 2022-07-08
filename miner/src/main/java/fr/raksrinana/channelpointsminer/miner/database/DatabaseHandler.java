package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.event.IEvent;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.event.IStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class DatabaseHandler implements IEventListener{
	@NotNull
	private IDatabase database;
	
	@Override
	public void onEvent(IEvent event){
		try{
			if(event instanceof StreamerAddedEvent e){
				addStreamer(e);
			}
			else if(event instanceof StreamUpEvent e){
				updateStreamer(e);
			}
			else if(event instanceof StreamDownEvent e){
				updateStreamer(e);
			}
			else if(event instanceof PointsEarnedEvent e){
				var pointsEarnedData = e.getPointsEarnedData();
				var reasonCode = pointsEarnedData.getPointGain().getReasonCode();
				updateBalance(e, pointsEarnedData.getBalance(), reasonCode.name());
			}
			else if(event instanceof PointsSpentEvent e){
				updateBalance(e, e.getPointsSpentData().getBalance(), null);
			}
			else if(event instanceof PredictionMadeEvent e){
				var placedPrediction = e.getPlacedPrediction();
				addPrediction(e, placedPrediction.getEventId(), "PREDICTED", Integer.toString(placedPrediction.getAmount()));
			}
			else if(event instanceof PredictionResultEvent e){
				addPrediction(e, e.getPredictionResultData().getPrediction().getEventId(), "RESULT", e.getGain());
			}
            else if(event instanceof EventUpdatedEvent e){
                handlePredictionUpdate(e.getEvent(), e.getStreamerUsername().orElseThrow());
            }
        }
		catch(Exception e){
			log.error("Failed to process database event", e);
		}
	}
	
	private void addStreamer(@NotNull IStreamerEvent event) throws SQLException{
		database.createChannel(event.getStreamerId(), event.getStreamerUsername().orElseThrow(() -> new IllegalStateException("No username present in streamer")));
	}
	
	private void updateStreamer(@NotNull IStreamerEvent event) throws SQLException{
		database.updateChannelStatusTime(event.getStreamerId(), event.getInstant());
	}
	
	private void updateBalance(@NotNull IStreamerEvent event, @NotNull Balance balance, @Nullable String reason) throws SQLException{
		database.addBalance(event.getStreamerId(), balance.getBalance(), reason, event.getInstant());
	}
	
	private void addPrediction(@NotNull IStreamerEvent event, @NotNull String eventId, @NotNull String type, @NotNull String description) throws SQLException{
		database.addPrediction(event.getStreamerId(), eventId, type, description, event.getInstant());
	}
    
    private void handlePredictionUpdate(@NotNull Event event, @NotNull String streamerUsername) throws SQLException{
        if(event.getStatus() == EventStatus.ACTIVE){
            log.debug("Prediction-Update: Event ACTIVE. Streamer: {}, Title: {}", streamerUsername, event.getTitle());
            for(var outcome: event.getOutcomes()){
                String badge = outcome.getBadge().getVersion();
                for(var predictor: outcome.getTopPredictors()){
                    database.addUserPrediction(predictor.getUserDisplayName(), streamerUsername, badge);
                }
            }
        }
        else if(event.getStatus() == EventStatus.CANCELED){
            log.debug("Prediction-Update: Event CANCELED. Streamer: {}, Title: {}", streamerUsername, event.getTitle());
            
            Instant ended = Optional.ofNullable(event.getEndedAt()).map(ChronoZonedDateTime::toInstant).orElse(Instant.now());
            database.cancelPrediction(event.getId(), event.getChannelId(), event.getTitle(), event.getCreatedAt().toInstant(), ended);
        }
        else if(event.getStatus() == EventStatus.RESOLVED){
            String winningOutcomeId = event.getWinningOutcomeId();
            Outcome winningOutcome = event.getOutcomes().stream()
                    .filter(e -> e.getId().equals(winningOutcomeId))
                    .findAny()
                    .orElseThrow();
            String winningOutcomeBadge = winningOutcome.getBadge().getVersion();
            
            log.debug("Prediction-Update: Event RESOLVED. Streamer: {}, Title: {}, Outcome: {}",
                    streamerUsername, event.getTitle(), winningOutcome.getTitle());
            
            Instant ended = Optional.ofNullable(event.getEndedAt()).map(ChronoZonedDateTime::toInstant).orElse(Instant.now());
            
            database.resolvePrediction(event.getId(), event.getChannelId(), event.getTitle(),
                    event.getCreatedAt().toInstant(), ended, winningOutcome.getTitle(), winningOutcomeBadge);
        }
    }
	
	@Override
	public void close(){
		database.close();
	}
}
