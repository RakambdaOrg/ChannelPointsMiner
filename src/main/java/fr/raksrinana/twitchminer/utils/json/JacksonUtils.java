package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.*;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;

public class JacksonUtils{
	private static JsonMapper mapper;
	
	@NotNull
	public static JsonMapper getMapper(){
		if(Objects.isNull(mapper)){
			mapper = JsonMapper.builder()
					.enable(ALLOW_TRAILING_COMMA)
					.enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
					.enable(ALLOW_COMMENTS)
					.visibility(FIELD, ANY)
					.visibility(GETTER, NONE)
					.visibility(SETTER, NONE)
					.visibility(CREATOR, NONE)
					.build();
		}
		return mapper;
	}
	
	public static <T> T read(@NotNull InputStream is, @NotNull TypeReference<T> type) throws IOException{
		return getMapper().readValue(is, type);
	}
	
	public static void write(@NotNull OutputStream os, @NotNull Object value) throws IOException{
		getMapper().writeValue(os, value);
	}
}
