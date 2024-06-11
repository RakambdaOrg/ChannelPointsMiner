package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("PlaybackAccessToken")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamPlaybackAccessToken extends GQLType{
	@JsonProperty("signature")
	@NotNull
	private String signature;
	@JsonProperty("value")
	@NotNull
	private String value;
}
