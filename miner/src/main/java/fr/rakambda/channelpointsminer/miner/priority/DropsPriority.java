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
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	
	@Override
	public boolean isDropsRelated(){
		return true;
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
		
		var possibleEntitlements = dropCampaign.getTimeBasedDrops().stream()
				.map(TimeBasedDrop::getBenefitEdges)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.collect(Collectors.groupingBy(dropBenefitEdge -> dropBenefitEdge.getBenefit().getId()))
				.values().stream()
				.map(dropBenefitEdges -> dropBenefitEdges.stream()
						.reduce((benefit1, benefit2) -> new DropBenefitEdge(
								benefit1.getBenefit(),
								benefit1.getEntitlementLimit() + benefit2.getEntitlementLimit(),
								Optional.ofNullable(benefit1.getClaimCount()).orElse(0) + Optional.ofNullable(benefit2.getClaimCount()).orElse(0))
						)
						.orElseThrow(() -> new IllegalStateException("Failed to get reduced benefit edge, shouldn't be possible"))
				)
				.toList();
		
		var result = dropCampaign.getTimeBasedDrops().stream().anyMatch(timeBasedDrop -> isValidDrop(miner, timeBasedDrop, possibleEntitlements));
		if(!result){
			log.trace("Campaign {} has no valid drops", dropCampaign.getId());
		}
		return result;
	}
	
	private boolean isValidDrop(@NotNull IMiner miner, @NotNull TimeBasedDrop timeBasedDrop, @NotNull List<DropBenefitEdge> possibleEntitlements){
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
				.anyMatch(timeBasedDropBenefitEdge -> {
					var dropBenefitEdge = possibleEntitlements.stream()
							.filter(possibleEntitlementDropBenefitEdge -> Objects.equals(timeBasedDropBenefitEdge.getBenefit().getId(), possibleEntitlementDropBenefitEdge.getBenefit().getId()))
							.findFirst()
							.orElse(timeBasedDropBenefitEdge);
					return isValidBenefit(miner, timeBasedDrop.getStartAt(), dropBenefitEdge);
				});
		if(!result){
			log.trace("Drop {} has no valid benefit", timeBasedDrop.getId());
		}
		return result;
	}
	
	private boolean isValidBenefit(@NotNull IMiner miner, @Nullable ZonedDateTime startAt, @NotNull DropBenefitEdge dropBenefitEdge){
		return Optional.ofNullable(miner.getMinerData().getInventory())
				.map(InventoryData::getCurrentUser)
				.map(User::getInventory)
				.map(Inventory::getGameEventDrops)
				.stream()
				.flatMap(Collection::stream)
				.filter(userDropReward -> Objects.equals(userDropReward.getId(), dropBenefitEdge.getBenefit().getId()))
				.findFirst()
				.map(userDropReward ->
						userDropReward.getTotalCount() < dropBenefitEdge.getEntitlementLimit()
								|| Optional.ofNullable(userDropReward.getLastAwardedAt())
								.map(last -> Optional.ofNullable(startAt).map(last::isBefore).orElse(false))
								.orElse(true)
				)
				.orElse(true);
	}
}
