package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@JsonTypeName("Tag")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class Tag extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("localizedName")
	private String localizedName;
	
	public Tag(){
		super("Tag");
	}
}
