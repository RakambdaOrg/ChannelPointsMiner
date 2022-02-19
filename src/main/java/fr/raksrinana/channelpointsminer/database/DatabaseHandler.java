package fr.raksrinana.channelpointsminer.database;

import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.event.IEvent;
import fr.raksrinana.channelpointsminer.event.IEventListener;
import fr.raksrinana.channelpointsminer.event.IStreamerEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;

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
				var reasonCode = e.getPointsEarnedData().getPointGain().getReasonCode();
				updateBalance(e, e.getPointsEarnedData().getBalance(), reasonCode.name());
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
	
	private void addPrediction(@NotNull IStreamerEvent e, @NotNull String eventId, @NotNull String type, @NotNull String description) throws SQLException{
		database.addPrediction(e.getStreamerId(), eventId, type, description, e.getInstant());
	}
	
	@Override
	public void close(){
		database.close();
	}
}
