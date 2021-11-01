package fr.raksrinana.twitchminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.Channel;
import fr.raksrinana.twitchminer.api.gql.data.types.Tag;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;

@JsonTypeName("drops")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DropsPriority extends StreamerPriority{
	@Override
	public int getScore(@NotNull IMiner miner, @NotNull Streamer streamer){
		if(streamer.isParticipateCampaigns()
				&& streamer.isStreamingGame()
				&& hasCampaigns(streamer)
				&& hasDropsTag(streamer)){
			return getScore();
		}
		
		return 0;
	}
	
	private boolean hasCampaigns(@NotNull Streamer streamer){
		return Optional.ofNullable(streamer.getDropsHighlightServiceAvailableDrops())
				.map(DropsHighlightServiceAvailableDropsData::getChannel)
				.map(Channel::getViewerDropCampaigns)
				.map(dropCampaigns -> !dropCampaigns.isEmpty())
				.orElse(false);
	}
	
	private boolean hasDropsTag(@NotNull Streamer streamer){
		return streamer.getTags().stream()
				.anyMatch(tag -> Objects.equals(tag.getId(), Tag.DROPS_TAG_ID));
	}
}
