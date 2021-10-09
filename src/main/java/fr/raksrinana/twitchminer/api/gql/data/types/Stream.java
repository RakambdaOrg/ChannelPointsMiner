package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.ZonedDateTime;
import java.util.List;

@JsonTypeName("Stream")
@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class Stream extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("viewersCount")
	private int viewersCount;
	@JsonProperty("tags")
	private List<Tag> tags;
	
	public Stream(){
		super("Stream");
	}
}
