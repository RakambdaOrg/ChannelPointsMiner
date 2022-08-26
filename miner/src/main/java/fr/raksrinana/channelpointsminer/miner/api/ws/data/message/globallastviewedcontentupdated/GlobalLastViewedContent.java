package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.globallastviewedcontentupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.LastViewedContent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class GlobalLastViewedContent{
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("last_viewed_content")
	private List<LastViewedContent> lastViewedContent = new ArrayList<>();
}
