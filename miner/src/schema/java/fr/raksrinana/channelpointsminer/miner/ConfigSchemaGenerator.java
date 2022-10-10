package fr.raksrinana.channelpointsminer.miner;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import fr.raksrinana.channelpointsminer.miner.config.Configuration;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;

@Log4j2
public class ConfigSchemaGenerator{
	public static void main(String[] args) throws IOException{
		var config = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
				.with(new JacksonModule())
				.build();
		var generator = new SchemaGenerator(config);
		
		var exampleFolder = Paths.get("miner/docs/modules/ROOT/examples");
		generate(generator, Configuration.class, exampleFolder.resolve("global-config-schema.json"));
		generate(generator, CookiesFileType.class, exampleFolder.resolve("global-config-cookies-schema.json"));
		generate(generator, StreamerSettings.class, exampleFolder.resolve("streamer-config-schema.json"));
	}
	
	private static void generate(@NotNull SchemaGenerator generator, @NotNull Class<?> klazz, @NotNull Path outPath) throws IOException{
		log.info("Generating JSON schema for class {} in {}", klazz, outPath.toAbsolutePath());
		var jsonSchema = generator.generateSchema(klazz);
		Files.writeString(outPath, jsonSchema.toPrettyString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
