package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("BroadcastSettings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class BroadcastSettings extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("title")
	@NotNull
	private String title;
	@JsonProperty("game")
	@Nullable
	private Game game;
}
