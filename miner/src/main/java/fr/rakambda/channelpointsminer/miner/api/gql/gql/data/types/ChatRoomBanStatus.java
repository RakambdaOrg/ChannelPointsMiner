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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@JsonTypeName("ChatRoomBanStatus")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ChatRoomBanStatus extends GQLType{
    @JsonProperty("bannedUser")
    @NotNull
    private User bannedUser;
    @JsonProperty("createdAt")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    @NotNull
    private ZonedDateTime createdAt;
    @JsonProperty("expiresAt")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    @Nullable
    private ZonedDateTime expiresAt;
    @JsonProperty("isPermanent")
    private boolean permanent;
    @JsonProperty("moderator")
    @Nullable
    private User moderator;
    @JsonProperty("reason")
    @Nullable
    private String reason;
}
