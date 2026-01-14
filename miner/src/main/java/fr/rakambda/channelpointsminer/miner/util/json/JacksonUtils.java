package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import static lombok.AccessLevel.PRIVATE;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;

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
					.enable(ORDER_MAP_ENTRIES_BY_KEYS)
					.enable(SORT_PROPERTIES_ALPHABETICALLY)
					.enable(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
					.enable(ALLOW_JAVA_COMMENTS)
					.enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
					.enable(ALLOW_TRAILING_COMMA)
					.disable(FAIL_ON_IGNORED_PROPERTIES)
					.disable(FAIL_ON_UNKNOWN_PROPERTIES)
					.disable(WRITE_DATES_AS_TIMESTAMPS).changeDefaultVisibility(vc -> vc
							.withVisibility(PropertyAccessor.FIELD, Visibility.ANY)
							.withVisibility(PropertyAccessor.GETTER, Visibility.NONE)
							.withVisibility(PropertyAccessor.SETTER, Visibility.NONE)
							.withVisibility(PropertyAccessor.CREATOR, Visibility.NONE)
					)
					.changeDefaultPropertyInclusion(ic -> ic.withValueInclusion(JsonInclude.Include.NON_NULL))
					.withConfigOverride(List.class, c -> c.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)))
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
	
	public static void write(@NonNull OutputStream os, @NonNull Object value){
		getMapper().writeValue(os, value);
	}
	
	public static String writeAsString(@NonNull Object value){
		return getMapper().writeValueAsString(value);
	}
}
