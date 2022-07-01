package fr.raksrinana.channelpointsminer.viewer.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelBalanceData{
	private String channelId;
	private String username;
	private Collection<BalanceData> balance;
}
