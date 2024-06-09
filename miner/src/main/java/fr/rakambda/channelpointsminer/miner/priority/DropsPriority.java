package fr.rakambda.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefitEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignSummary;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Inventory;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
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
@JsonClassDescription("Return a score if a drop campaign may be progressed by watching this stream.")
public class DropsPriority extends IStreamerPriority{
	@Override
	public int getScore(@NotNull IMiner miner, @NotNull Streamer streamer){
		if(streamer.isParticipateCampaigns()
				&& streamer.isStreamingGame()
				&& hasCampaigns(miner, streamer)){
			return getScore();
		}
		
		return 0;
	}
	
	private boolean hasCampaigns(@NotNull IMiner miner, @NotNull Streamer streamer){
		return Optional.ofNullable(streamer.getDropsHighlightServiceAvailableDrops())
				.map(DropsHighlightServiceAvailableDropsData::getChannel)
				.map(Channel::getViewerDropCampaigns)
				.stream()
				.flatMap(Collection::stream)
				.anyMatch(dropCampaign -> isValidCampaign(miner, streamer, dropCampaign));
	}
	
	private boolean isValidCampaign(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull DropCampaign dropCampaign){
		var now = TimeFactory.nowZoned();
		
		if(Optional.ofNullable(dropCampaign.getStartAt()).map(date -> date.isAfter(now)).orElse(false)){
			log.trace("Campaign {} hasn't started", dropCampaign.getId());
			return false;
		}
		if(Optional.ofNullable(dropCampaign.getEndAt()).map(date -> date.isBefore(now)).orElse(false)){
			log.trace("Campaign {} already ended", dropCampaign.getId());
			return false;
		}
		if(streamer.isExcludeSubscriberDrops() && Optional.ofNullable(dropCampaign.getSummary()).map(DropCampaignSummary::isIncludesSubRequirement).orElse(false)){
			log.trace("Campaign {} requires subscriptions", dropCampaign.getId());
			return false;
		}
		
		var result = dropCampaign.getTimeBasedDrops().stream().anyMatch(timeBasedDrop -> isValidDrop(miner, timeBasedDrop));
		if(!result){
			log.trace("Campaign {} has no valid drops", dropCampaign.getId());
		}
		return result;
	}
	
	private boolean isValidDrop(@NotNull IMiner miner, @NotNull TimeBasedDrop timeBasedDrop){
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
				.anyMatch(dropBenefitEdge -> isValidBenefit(miner, dropBenefitEdge));
		if(!result){
			log.trace("Drop {} has no valid benefit", timeBasedDrop.getId());
		}
		return result;
	}
	
	private boolean isValidBenefit(@NotNull IMiner miner, @NotNull DropBenefitEdge dropBenefitEdge){
		return Optional.ofNullable(miner.getMinerData().getInventory())
				.map(InventoryData::getCurrentUser)
				.map(User::getInventory)
				.map(Inventory::getGameEventDrops)
				.stream()
				.flatMap(Collection::stream)
				.filter(userDropReward -> Objects.equals(userDropReward.getId(), dropBenefitEdge.getBenefit().getId()))
				.findFirst()
				.map(userDropReward -> userDropReward.getTotalCount() < dropBenefitEdge.getEntitlementLimit())
				.orElse(true);
	}
}
