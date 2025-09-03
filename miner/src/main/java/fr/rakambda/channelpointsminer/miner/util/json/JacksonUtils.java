package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.Nulls.AS_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.CREATOR;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.SETTER;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JacksonUtils{
	private static JsonMapper mapper;
	
	@NonNull
	public static <T> T read(@NonNull InputStream is, @NonNull TypeReference<T> type) throws IOException{
		return getMapper().readValue(is, type);
	}
	
	@NonNull
	public static JsonMapper getMapper(){
		if(Objects.isNull(mapper)){
			mapper = JsonMapper.builder()
					.enable(SORT_PROPERTIES_ALPHABETICALLY)
					.enable(ALLOW_TRAILING_COMMA)
					.enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
					.enable(ALLOW_COMMENTS)
					.disable(FAIL_ON_IGNORED_PROPERTIES)
					.disable(FAIL_ON_UNKNOWN_PROPERTIES)
					.disable(WRITE_DATES_AS_TIMESTAMPS)
					.visibility(FIELD, ANY)
					.visibility(GETTER, NONE)
					.visibility(SETTER, NONE)
					.visibility(CREATOR, NONE)
					.serializationInclusion(NON_NULL)
					.withConfigOverride(List.class, o -> o.setSetterInfo(Value.forValueNulls(AS_EMPTY)))
					.addModule(new JavaTimeModule())
					.build();
		}
		return mapper;
	}
	
	@NonNull
	public static <T> T read(@NonNull String value, @NonNull TypeReference<T> type) throws IOException{
		return getMapper().readValue(value, type);
	}
	
	@NonNull
	public static <T> T update(InputStream is, T object) throws IOException{
		return getMapper().readerForUpdating(object).readValue(is);
	}
	
	public static void write(@NonNull OutputStream os, @NonNull Object value) throws IOException{
		getMapper().writeValue(os, value);
	}
	
	public static String writeAsString(@NonNull Object value) throws JsonProcessingException{
		return getMapper().writeValueAsString(value);
	}
}
