package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JacksonUtils{
	private static JsonMapper mapper;
	
	@NotNull
	public static <T> T read(@NotNull InputStream is, @NotNull TypeReference<T> type) throws IOException{
		return getMapper().readValue(is, type);
	}
	
	@NotNull
	public static JsonMapper getMapper(){
		if(Objects.isNull(mapper)){
			mapper = JsonMapper.builder()
					.enable(SORT_PROPERTIES_ALPHABETICALLY)
					.enable(ALLOW_TRAILING_COMMA)
					.enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
					.enable(ALLOW_COMMENTS)
					.disable(FAIL_ON_IGNORED_PROPERTIES)
					.disable(FAIL_ON_UNKNOWN_PROPERTIES)
					.visibility(FIELD, ANY)
					.visibility(GETTER, NONE)
					.visibility(SETTER, NONE)
					.visibility(CREATOR, NONE)
					.serializationInclusion(NON_NULL)
					.withConfigOverride(List.class, o -> o.setSetterInfo(Value.forValueNulls(AS_EMPTY)))
					.build();
		}
		return mapper;
	}
	
	@NotNull
	public static <T> T read(@NotNull String value, @NotNull TypeReference<T> type) throws IOException{
		return getMapper().readValue(value, type);
	}
	
	@NotNull
	public static <T> T update(InputStream is, T object) throws IOException{
		return getMapper().readerForUpdating(object).readValue(is);
	}
	
	public static void write(@NotNull OutputStream os, @NotNull Object value) throws IOException{
		getMapper().writeValue(os, value);
	}
	
	public static String writeAsString(@NotNull Object value) throws JsonProcessingException{
		return getMapper().writeValueAsString(value);
	}
}
