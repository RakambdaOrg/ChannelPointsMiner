package fr.raksrinana.twitchminer.miner.priority;

import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.Channel;
import fr.raksrinana.twitchminer.api.gql.data.types.DropCampaign;
import fr.raksrinana.twitchminer.api.gql.data.types.Tag;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropsPriorityTest{
	private static final int SCORE = 50;
	private static final String DROPS_TAG_ID = "c2542d6d-cd10-4532-919b-3d19f30a768b";
	
	private final DropsPriority tested = DropsPriority.builder().score(SCORE).build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	@Mock
	private DropsHighlightServiceAvailableDropsData dropsHighlightServiceAvailableDropsData;
	@Mock
	private Channel channel;
	@Mock
	private DropCampaign dropCampaign;
	@Mock
	private Tag tag;
	
	@BeforeEach
	void setUp(){
		lenient().when(streamer.isParticipateCampaigns()).thenReturn(true);
		lenient().when(streamer.isStreamingGame()).thenReturn(true);
		lenient().when(streamer.getTags()).thenReturn(List.of(tag));
		lenient().when(streamer.getDropsHighlightServiceAvailableDrops()).thenReturn(dropsHighlightServiceAvailableDropsData);
		lenient().when(dropsHighlightServiceAvailableDropsData.getChannel()).thenReturn(channel);
		lenient().when(channel.getViewerDropCampaigns()).thenReturn(List.of(dropCampaign));
		lenient().when(tag.getId()).thenReturn(DROPS_TAG_ID);
	}
	
	@Test
	void streamerNotParticipatingCampaigns(){
		when(streamer.isParticipateCampaigns()).thenReturn(false);
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void streamerNotStreamingGame(){
		when(streamer.isStreamingGame()).thenReturn(false);
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void streamerWithoutDropsTag(){
		when(streamer.getTags()).thenReturn(List.of());
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void streamerNoDropsHighlights(){
		when(streamer.getDropsHighlightServiceAvailableDrops()).thenReturn(null);
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void streamerNoDrops(){
		when(channel.getViewerDropCampaigns()).thenReturn(List.of());
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void streamerHasDrops(){
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
	}
}