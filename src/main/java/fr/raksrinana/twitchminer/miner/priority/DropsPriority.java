package fr.raksrinana.twitchminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
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
		if(!streamer.isParticipateCampaigns()){
			return 0;
		}
		
		var dropsHighlightServiceAvailableDrops = streamer.getDropsHighlightServiceAvailableDrops();
		if(Objects.isNull(dropsHighlightServiceAvailableDrops)){
			return 0;
		}
		
		var gameOptional = streamer.getGame();
		if(gameOptional.isEmpty()){
			return 0;
		}
		var game = gameOptional.get();
		
		var inventoryOptional = Optional.ofNullable(miner.getMinerData().getInventory())
				.map(InventoryData::getCurrentUser)
				.map(User::getInventory);
		if(inventoryOptional.isEmpty()){
			return 0;
		}
		var inventory = inventoryOptional.get();
		
		var now = TimeFactory.nowZoned();
		var streamerCampaigns = dropsHighlightServiceAvailableDrops.getChannel().getViewerDropCampaigns();
		var validCampaigns = inventory.getDropCampaignsInProgress().stream()
				.filter(dropCampaign -> dropCampaign.getStatus() == DropCampaignStatus.ACTIVE)
				.filter(dropCampaign -> dropCampaign.getStartAt().isBefore(now))
				.filter(dropCampaign -> dropCampaign.getEndAt().isAfter(now))
				.filter(dropCampaign -> streamerHasCampaign(dropCampaign, streamerCampaigns))
				.filter(dropCampaign -> Objects.equals(dropCampaign.getGame().getId(), game.getId()))
				.filter(dropCampaign -> isAllowed(dropCampaign.getAllow(), streamer))
				.filter(dropCampaign -> dropCampaign.getTimeBasedDrops().stream().anyMatch(this::canBeProgressed))
				.toList();
		
		return validCampaigns.isEmpty() ? 0 : getScore();
	}
	
	private boolean streamerHasCampaign(@NotNull DropCampaign dropCampaign, @NotNull List<DropCampaign> streamerCampaigns){
		return streamerCampaigns.stream().anyMatch(streamerCampaign -> Objects.equals(streamerCampaign.getId(), dropCampaign.getId()));
	}
	
	private boolean isAllowed(@Nullable DropCampaignACL dropCampaignACL, @NotNull Streamer streamer){
		if(Objects.isNull(dropCampaignACL) || dropCampaignACL.getChannels().isEmpty()){
			return true;
		}
		return dropCampaignACL.getChannels().stream().anyMatch(channel -> Objects.equals(channel.getId(), streamer.getId()));
	}
	
	private boolean canBeProgressed(@NotNull TimeBasedDrop timeBasedDrop){
		var timeBasedDropSelf = timeBasedDrop.getSelf();
		if(Objects.isNull(timeBasedDropSelf)){
			return false;
		}
		
		return timeBasedDropSelf.isHasPreconditionsMet()
				&& !timeBasedDropSelf.isClaimed()
				&& timeBasedDropSelf.getCurrentMinutesWatched() < timeBasedDrop.getRequiredMinutesWatched();
	}
}
