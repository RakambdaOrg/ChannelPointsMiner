package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.ZonedDateTime;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class ISO8601ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>{
	@Override
	@Nullable
	public ZonedDateTime deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext context) throws IOException{
		return ZonedDateTime.parse(jsonParser.getValueAsString(), ISO_DATE_TIME).withZoneSameInstant(systemDefault());
	}
}
