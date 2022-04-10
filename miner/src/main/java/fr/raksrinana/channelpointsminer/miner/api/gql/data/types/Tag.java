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

@JsonTypeName("Tag")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class Tag extends GQLType{
	public static final String DROPS_TAG_ID = "c2542d6d-cd10-4532-919b-3d19f30a768b";
	
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("localizedName")
	@NotNull
	private String localizedName;
}
