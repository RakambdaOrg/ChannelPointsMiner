package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.ZonedDateTime;

@JsonTypeName("UserDropReward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserDropReward extends GQLType{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("totalCount")
	private int totalCount;
	@JsonProperty("lastAwardedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime lastAwardedAt;
}
