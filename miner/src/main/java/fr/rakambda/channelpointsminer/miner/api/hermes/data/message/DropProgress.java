package fr.rakambda.channelpointsminer.miner.api.hermes.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.dropprogress.DropProgressData;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("drop-progress")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropProgress extends IHermesMessage {
	@JsonProperty("data")
	@NotNull
	private DropProgressData data;
}
