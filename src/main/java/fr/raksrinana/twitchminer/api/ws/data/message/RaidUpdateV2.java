package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Raid;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("raid_update_v2")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaidUpdateV2 extends Message{
	@JsonProperty("raid")
	@NotNull
	private Raid raid;
}
