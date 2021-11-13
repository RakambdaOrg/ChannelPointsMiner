package fr.raksrinana.channelpointsminer.runnable;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
public class StreamerConfigurationReload implements Runnable{
	@NotNull
	private final IMiner miner;
	@NotNull
	private final StreamerSettingsFactory streamerSettingsFactory;
	private final boolean loadFollows;
	
	@Override
	public void run(){
		var streamers = getAllStreamers();
		removeStreamers(streamers);
		updateStreamers(streamers);
		addStreamers(streamers);
	}
	
	@NotNull
	private List<Streamer> getAllStreamers(){
		var streamers = new ArrayList<>(getStreamersFromConfiguration());
		
		var excludedIds = streamers.stream().map(Streamer::getId).toList();
		streamers.addAll(getStreamersFromFollows(excludedIds));
		
		return streamers;
	}
	
	private void removeStreamers(@NotNull List<Streamer> newStreamers){
		miner.getStreamers().stream()
				.filter(oldStreamer -> !newStreamers.contains(oldStreamer))
				.forEach(miner::removeStreamer);
	}
	
	private void updateStreamers(@NotNull List<Streamer> newStreamers){
		miner.getStreamers().stream()
				.map(oldStreamer -> newStreamers.stream()
						.filter(s -> Objects.equals(s, oldStreamer))
						.findAny()
						.map(streamer -> Map.entry(oldStreamer, streamer)))
				.flatMap(Optional::stream)
				.forEach(entry -> {
					var old = entry.getKey();
					var update = entry.getValue();
					
					old.setSettings(update.getSettings());
					miner.updateStreamer(old);
				});
	}
	
	private void addStreamers(@NotNull List<Streamer> newStreamers){
		newStreamers.stream()
				.filter(newStreamer -> !miner.getStreamers().contains(newStreamer))
				.forEach(miner::addStreamer);
	}
	
	@NotNull
	private List<Streamer> getStreamersFromConfiguration(){
		log.debug("Loading streamers from configuration");
		return streamerSettingsFactory.getStreamerConfigs()
				.map(Path::getFileName)
				.map(Path::toString)
				.map(name -> name.substring(0, name.length() - ".json".length()))
				.map(this::createStreamer)
				.flatMap(Optional::stream)
				.toList();
	}
	
	@NotNull
	private List<Streamer> getStreamersFromFollows(@NotNull Collection<String> excludedIds){
		if(!loadFollows){
			return List.of();
		}
		
		log.debug("Loading streamers from follow list");
		return miner.getGqlApi().allChannelFollows().stream()
				.map(user -> {
					var streamerId = user.getId();
					var streamerName = user.getLogin();
					return new Streamer(streamerId, streamerName, streamerSettingsFactory.createStreamerSettings(streamerName));
				})
				.toList();
	}
	
	@NotNull
	private Optional<Streamer> createStreamer(@NotNull String username){
		var streamer = miner.getGqlApi().reportMenuItem(username)
				.map(GQLResponse::getData)
				.map(ReportMenuItemData::getUser)
				.map(user -> new Streamer(user.getId(), username, streamerSettingsFactory.createStreamerSettings(username)));
		if(streamer.isEmpty()){
			log.error("Failed to get streamer {}", username);
		}
		return streamer;
	}
}
