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
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsEmoteModification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsEmoteModification extends GQLType{
    @JsonProperty("id")
    private String id;
    @JsonProperty("emote")
    private CommunityPointsEmote emote;
    @JsonProperty("modifierIconDark")
    private CommunityPointsImage modifierIconDark;
    @JsonProperty("modifierIconLight")
    private CommunityPointsImage modifierIconLight;
    @JsonProperty("title")
    private String title;
    @JsonProperty("globallyUpdatedForIndicatorAt")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime globallyUpdatedForIndicatorAt;
    @JsonProperty("modifier")
    private CommunityPointsEmoteModifier modifier;
}
