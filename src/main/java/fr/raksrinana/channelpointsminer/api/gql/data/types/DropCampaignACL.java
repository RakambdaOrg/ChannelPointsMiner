package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("DropCampaignACL")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropCampaignACL extends GQLType{
	@JsonProperty("channels")
	@NotNull
	@Builder.Default
	private List<Channel> channels = new ArrayList<>();
}
