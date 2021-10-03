package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;

@JsonTypeName("Stream")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class Stream extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("viewersCount")
	private int viewersCount;
	@JsonProperty("tags")
	private List<Tag> tags;
	
	public Stream(){
		super("Stream");
	}
}
