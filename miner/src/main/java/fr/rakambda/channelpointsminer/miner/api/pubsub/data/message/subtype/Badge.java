package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Badge{
	@JsonProperty("version")
	@NonNull
	private String version;
}
