package fr.rakambda.channelpointsminer.miner.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.activemultipliersupdated.ActiveMultipliersUpdatedData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("active-multipliers-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class ActiveMultipliersUpdated extends IPubSubMessage{
	@JsonProperty("data")
	@NotNull
	private ActiveMultipliersUpdatedData data;
}
