package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Raid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("raid_go_v2")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class RaidGoV2 extends Message{
	@JsonProperty("raid")
	@NotNull
	private Raid raid;
}
