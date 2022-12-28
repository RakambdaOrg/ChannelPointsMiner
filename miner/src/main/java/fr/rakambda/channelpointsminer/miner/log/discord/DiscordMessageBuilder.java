package fr.rakambda.channelpointsminer.miner.log.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Author;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Embed;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Footer;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import fr.rakambda.channelpointsminer.miner.config.DiscordEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiscordMessageBuilder{
	@NotNull
	public Webhook createSimpleMessage(@NotNull ILoggableEvent event, @Nullable DiscordEventConfiguration config){
		return Webhook.builder().content(formatMessage(event, event.getDefaultFormat())).build();
	}
	
	@NotNull
	public Webhook createEmbedMessage(@NotNull ILoggableEvent event, @Nullable DiscordEventConfiguration config){
		var fields = event.getEmbedFields().entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(e -> Field.builder().name(e.getKey()).value(formatMessage(event, "{%s}".formatted(e.getValue()))).build())
				.collect(Collectors.toList());
		
		var embed = Embed.builder()
				.author(getEmbedAuthor(event))
				.footer(getEmbedFooter(event))
				.color(getEmbedColor(event))
				.description(formatMessage(event, event.getDefaultFormat()))
				.fields(fields)
				.build();
		
		return Webhook.builder()
				.embeds(List.of(embed))
				.build();
	}
	
	@NotNull
	private String formatMessage(@NotNull StringLookup event, @NotNull String format){
		var substitutor = new StringSubstitutor(event, "{", "}", '$');
		return substitutor.replace(format);
	}
	
	@Nullable
	protected Author getEmbedAuthor(@NotNull StringLookup event){
		return Optional.ofNullable(event.lookup(EventVariableKey.STREAMER))
				.map(username -> Author.builder().name(username)
						.url(event.lookup(EventVariableKey.STREAMER_URL))
						.iconUrl(event.lookup(EventVariableKey.STREAMER_PROFILE_PICTURE_URL))
						.build()
				)
				.orElse(null);
	}
	
	@Nullable
	protected Footer getEmbedFooter(@NotNull StringLookup event){
		return Optional.ofNullable(event.lookup(EventVariableKey.USERNAME))
				.map(username -> Footer.builder().text(username).build())
				.orElse(null);
	}
	
	@Nullable
	protected Integer getEmbedColor(@NotNull StringLookup event){
		return Optional.ofNullable(event.lookup(EventVariableKey.COLOR))
				.map(Integer::parseInt)
				.orElse(null);
	}
}
