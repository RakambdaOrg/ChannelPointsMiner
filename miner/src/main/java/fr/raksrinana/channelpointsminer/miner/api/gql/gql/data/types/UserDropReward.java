package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.time.ZonedDateTime;

@JsonTypeName("UserDropReward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserDropReward extends GQLType{
	@JsonProperty("game")
	@NotNull
	private Game game;
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("imageURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL imageUrl;
	@JsonProperty("isConnected")
	private boolean isConnected;
	@JsonProperty("lastAwardedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime lastAwardedAt;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("requiredAccountLink")
	@NotNull
	private String requiredAccountLink;
	@JsonProperty("totalCount")
	private int totalCount;
}
