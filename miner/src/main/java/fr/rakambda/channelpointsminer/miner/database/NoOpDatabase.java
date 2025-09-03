package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class NoOpDatabase implements IDatabase{
	@Override
	public void initDatabase(){
	}
	
	@Override
	public void createChannel(@NonNull String channelId, @NonNull String username){
	}
	
	@Override
	public void updateChannelStatusTime(@NonNull String channelId, @NonNull Instant instant){
	}
	
	@Override
	public void addBalance(@NonNull String channelId, int balance, @Nullable String reason, @NonNull Instant instant){
	}
	
	@Override
	public void addPrediction(@NonNull String channelId, @NonNull String eventId, @NonNull String type, @NonNull String description, @NonNull Instant instant){
	}
	
	@Override
	public int addUserPrediction(@NonNull String username, @NonNull String streamerName, @NonNull String badge){
		return -1;
	}
	
	@Override
	public void cancelPrediction(@NonNull Event event){
	}
	
	@Override
	public void resolvePrediction(@NonNull Event event, @NonNull String outcome, @NonNull String badge, double returnOnInvestment){
	}
	
	@Override
	@NonNull
	public Optional<String> getStreamerIdFromName(@NonNull String channelName){
		return Optional.empty();
	}
	
	@Override
	public void deleteUserPredictionsForChannel(@NonNull String channelId){
	}
	
	@Override
	@NonNull
	public Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NonNull String channelId, int minBetsPlacedByUser){
		return List.of();
	}
	
	@Override
	public void close(){
	}
	
	@Override
	public void deleteAllUserPredictions(){
	}
}
