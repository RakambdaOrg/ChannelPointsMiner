package fr.raksrinana.channelpointsminer.miner.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("FollowConnection")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class FollowConnection extends GQLType{
	@JsonProperty("edges")
	@Builder.Default
	@NotNull
	private List<FollowEdge> edges = new ArrayList<>();
	@JsonProperty("pageInfo")
	@NotNull
	private PageInfo pageInfo;
}
