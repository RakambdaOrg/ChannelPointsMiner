package fr.rakambda.channelpointsminer.miner.api.gql.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
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
	
	public IGQLOperation(@NonNull String operationName){
		this.operationName = operationName;
	}
	
	protected void addPersistedQueryExtension(@NonNull PersistedQueryExtension persistedQueryExtension){
		addExtension(PERSISTED_QUERY_EXTENSION_NAME, persistedQueryExtension);
	}
	
	protected void addExtension(@NonNull String key, @NonNull Object extension){
		extensions.put(key, extension);
	}
	
	protected void addVariable(@NonNull String key, @NonNull Object object){
		variables.put(key, object);
	}
	
	@NonNull
	public abstract GenericType<GQLResponse<T>> getResponseType();
}
