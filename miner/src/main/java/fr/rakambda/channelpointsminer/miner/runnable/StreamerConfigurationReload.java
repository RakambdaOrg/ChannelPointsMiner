package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.ReportMenuItemData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerUnknownEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.runnable.data.StreamerResult;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class StreamerConfigurationReload implements Runnable {
    @NonNull
    private final IMiner miner;
    @NonNull
    private final IEventManager eventManager;
	@NonNull
    private final StreamerSettingsFactory streamerSettingsFactory;
    private final boolean loadFollows;

    @Override
    public void run() {
        try (var ignored = LogContext.with(miner)) {
            log.debug("Updating streamer list");
            var streamers = getAllStreamers();
            removeStreamers(streamers);
            updateStreamers(streamers);
            addStreamers(streamers);
        }
    }

    @NonNull
    private Map<String, StreamerResult> getAllStreamers() {
        var streamers = new HashMap<String, StreamerResult>();
        streamers.putAll(getStreamersFromFollows(streamers.keySet()));
        streamers.putAll(getStreamersFromConfiguration(streamers.keySet()));
        return streamers;
    }

    private void removeStreamers(@NonNull Map<String, StreamerResult> newStreamers) {
        miner.getStreamers().stream()
                .filter(oldStreamer -> !newStreamers.containsKey(oldStreamer.getUsername().toLowerCase(Locale.ROOT)))
                .forEach(miner::removeStreamer);
        newStreamers.values().stream()
                .map(this::getIfDisabled)
                .flatMap(Optional::stream)
                .forEach(miner::removeStreamer);
    }

    @NonNull
    private Optional<Streamer> getIfDisabled(@NonNull StreamerResult streamerResult) {
        var settings = streamerResult.getStreamerSettingsSupplier().get();
        if (settings.isEnabled()) {
            return Optional.empty();
        }
        return streamerResult.getStreamerSupplier().apply(settings);
    }

    private void updateStreamers(@NonNull Map<String, StreamerResult> newStreamers) {
        miner.getStreamers().stream()
                .map(oldStreamer -> newStreamers.entrySet().stream()
                        .filter(entry -> Objects.equals(entry.getKey(), oldStreamer.getUsername().toLowerCase(Locale.ROOT)))
                        .findAny()
                        .map(Map.Entry::getValue)
                        .map(result -> {
                            var settings = result.getStreamerSettingsSupplier().get();
                            if (!settings.isEnabled()) {
                                return null;
                            }
                            var streamer = result.getStreamerSupplier().apply(settings);
	                        return streamer.map(value -> Map.entry(oldStreamer, value)).orElse(null);
                        })
                )
                .flatMap(Optional::stream)
                .forEach(entry -> {
                    var old = entry.getKey();
                    var update = entry.getValue();

                    old.setSettings(update.getSettings());
                    miner.updateStreamer(old);
                });
    }

    private void addStreamers(@NonNull Map<String, StreamerResult> newStreamers) {
        var currentMinerNames = miner.getStreamers().stream()
                .map(Streamer::getUsername)
                .map(String::toLowerCase)
                .toList();
        newStreamers.entrySet().stream()
                .filter(entry -> !currentMinerNames.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(result -> {
                    var settings = result.getStreamerSettingsSupplier().get();
                    if (!settings.isEnabled()) {
                        return Optional.<Streamer>empty();
                    }
                    return result.getStreamerSupplier().apply(settings);
                })
                .flatMap(Optional::stream)
                .forEach(miner::addStreamer);
    }

    @NonNull
    private Map<String, StreamerResult> getStreamersFromFollows(@NonNull Collection<String> excludedNames) {
        if (!loadFollows) {
            return Map.of();
        }

        Function<String, StreamerSettings> settingsFunction = streamerSettingsFactory::createStreamerSettings;

        log.debug("Loading streamers from follow list");
        return miner.getGqlApi().allChannelFollows().stream()
                .filter(user -> !excludedNames.contains(user.getLogin().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toMap(user -> user.getLogin().toLowerCase(Locale.ROOT), user -> {
                    var streamerName = user.getLogin();
                    return new StreamerResult(
                            streamerName,
                            () -> settingsFunction.apply(streamerName),
                            settings -> Optional.of(new Streamer(user.getId(), streamerName, settings)));
                }));
    }

    @NonNull
    private Map<String, StreamerResult> getStreamersFromConfiguration(@NonNull Collection<String> excludedNames) {
        log.debug("Loading streamers from configuration");
        return streamerSettingsFactory.getStreamerConfigs()
                .map(Path::getFileName)
                .map(Path::toString)
                .map(name -> name.substring(0, name.length() - ".json".length()))
                .filter(name -> !excludedNames.contains(name.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toMap(
                        name -> name.toLowerCase(Locale.ROOT),
                        name -> new StreamerResult(
                                name,
                                () -> streamerSettingsFactory.createStreamerSettings(name),
                                settings -> getStreamerId(name).map(id -> new Streamer(id, name, settings))),
                        (existing, replacement) -> {
                            log.warn("Streamer {} has duplicate json files, only one will be kept", existing.getUsername());
                            return existing;
                        }));
    }

    @NonNull
    private Optional<String> getStreamerId(@NonNull String username) {
        var id = miner.getGqlApi().reportMenuItem(username)
                .map(GQLResponse::getData)
                .map(ReportMenuItemData::getUser)
                .map(User::getId);
        if (id.isEmpty()) {
	        eventManager.onEvent(new StreamerUnknownEvent(username, TimeFactory.now()));
        }
        return id;
    }
}
