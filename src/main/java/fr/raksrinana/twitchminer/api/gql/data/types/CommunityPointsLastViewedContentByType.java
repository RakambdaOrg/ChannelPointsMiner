package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsLastViewedContentByType")
@Getter
@AllArgsConstructor
public class CommunityPointsLastViewedContentByType extends GQLType{
	@JsonProperty("contentType")
	private ContentType contentType;
	@JsonProperty("lastViewedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime lastViewedAt;
	
	public CommunityPointsLastViewedContentByType(){
		super("CommunityPointsLastViewedContentByType");
	}
}
