package fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ContentId;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ContentType;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LastViewedContent{
	@JsonProperty("content_type")
	@NotNull
	private ContentType contentType;
	@JsonProperty("content_id")
	@NotNull
	private ContentId contentId;
	@JsonProperty("last_viewed_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime lastViewedAt;
}
