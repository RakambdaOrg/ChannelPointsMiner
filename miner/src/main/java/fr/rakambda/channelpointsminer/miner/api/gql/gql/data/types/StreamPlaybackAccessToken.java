package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jspecify.annotations.NonNull;

@JsonTypeName("PlaybackAccessToken")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamPlaybackAccessToken extends GQLType{
	@JsonProperty("signature")
	@NonNull
	private String signature;
	@JsonProperty("value")
	@NonNull
	private String value;
}
