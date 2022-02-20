package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
@ToString
public abstract class IGQLOperation<T>{
	private static final String PERSISTED_QUERY_EXTENSION_NAME = "persistedQuery";
	
	@JsonProperty("operationName")
	private String operationName;
	@JsonProperty("extensions")
	private Map<String, Object> extensions = new HashMap<>();
	@JsonProperty("variables")
	private Map<String, Object> variables = new HashMap<>();
	
	public IGQLOperation(@NotNull String operationName){
		this.operationName = operationName;
	}
	
	protected void addPersistedQueryExtension(@NotNull PersistedQueryExtension persistedQueryExtension){
		addExtension(PERSISTED_QUERY_EXTENSION_NAME, persistedQueryExtension);
	}
	
	protected void addExtension(@NotNull String key, @NotNull Object extension){
		extensions.put(key, extension);
	}
	
	protected void addVariable(@NotNull String key, @NotNull Object object){
		variables.put(key, object);
	}
	
	@NotNull
	public abstract GenericType<GQLResponse<T>> getResponseType();
}
