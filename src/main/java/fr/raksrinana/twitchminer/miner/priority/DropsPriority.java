package fr.raksrinana.twitchminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@JsonTypeName("drops")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Log4j2
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
				.stream()
				.flatMap(Collection::stream)
				.anyMatch(this::isValidCampaign);
	}
	
	private boolean isValidCampaign(@NotNull DropCampaign dropCampaign){
		var now = TimeFactory.nowZoned();
		
		if(Optional.ofNullable(dropCampaign.getStartAt()).map(date -> date.isAfter(now)).orElse(false)){
			log.trace("Campaign {} hasn't started", dropCampaign.getId());
			return false;
		}
		if(Optional.ofNullable(dropCampaign.getEndAt()).map(date -> date.isBefore(now)).orElse(false)){
			log.trace("Campaign {} already ended", dropCampaign.getId());
			return false;
		}
		
		var result = dropCampaign.getTimeBasedDrops().stream().anyMatch(this::isValidDrop);
		if(!result){
			log.trace("Campaign {} has no valid drops", dropCampaign.getId());
		}
		return result;
	}
	
	private boolean isValidDrop(@NotNull TimeBasedDrop timeBasedDrop){
		var now = TimeFactory.nowZoned();
		
		if(Optional.ofNullable(timeBasedDrop.getStartAt()).map(date -> date.isAfter(now)).orElse(false)){
			log.trace("Drop {} hasn't started", timeBasedDrop.getId());
			return false;
		}
		if(Optional.ofNullable(timeBasedDrop.getEndAt()).map(date -> date.isBefore(now)).orElse(false)){
			log.trace("Drop {} already ended", timeBasedDrop.getId());
			return false;
		}
		
		var result = Optional.ofNullable(timeBasedDrop.getBenefitEdges())
				.stream()
				.flatMap(Collection::stream)
				.anyMatch(this::isValidBenefit);
		if(!result){
			log.trace("Drop {} has no valid benefit", timeBasedDrop.getId());
		}
		return result;
	}
	
	private boolean isValidBenefit(@NotNull DropBenefitEdge dropBenefitEdge){
		return dropBenefitEdge.getClaimCount() < dropBenefitEdge.getEntitlementLimit();
	}
	
	private boolean hasDropsTag(@NotNull Streamer streamer){
		return streamer.getTags().stream()
				.anyMatch(tag -> Objects.equals(tag.getId(), Tag.DROPS_TAG_ID));
	}
}
