package fr.rakambda.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;

public abstract class JacksonTest{
	private JsonMapper mapper;
	
	@BeforeEach
	void setUp(){
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
						.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
						.withVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
						.withVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
						.withVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)
				)
				.changeDefaultPropertyInclusion(ic -> ic.withValueInclusion(JsonInclude.Include.NON_NULL))
				.withConfigOverride(List.class, c -> c.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)))
				.build();
	}
	
	protected JsonMapper getMapper(){
		return mapper;
	}
}
