package fr.rakambda.channelpointsminer.miner;

import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import fr.rakambda.channelpointsminer.miner.config.Configuration;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;

@Log4j2
public class ConfigSchemaGenerator{
	private static final Map<Class<?>, Class<?>> CLASS_MAPPINGS = Map.of(
			URL.class, String.class,
			Path.class, String.class
	);
	
	public static void main(String[] args) throws IOException{
		var configBuilder = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON).with(new JacksonModule());
		
		configBuilder.forTypesInGeneral().withCustomDefinitionProvider((javaType, context) ->
				CLASS_MAPPINGS.entrySet().stream()
						.filter(entry -> javaType.isInstanceOf(entry.getKey()))
						.findFirst()
						.map(entry -> new CustomDefinition(context.createDefinitionReference(context.getTypeContext().resolve(entry.getValue()))))
						.orElse(null)
		);
		
		var config = configBuilder.build();
		var generator = new SchemaGenerator(config);
		
		var exampleFolder = Paths.get("miner/docs/modules/ROOT/examples");
		generate(generator, Configuration.class, exampleFolder.resolve("global-config-schema.json"));
		generate(generator, CookiesFileType.class, exampleFolder.resolve("global-config-cookies-schema.json"));
		generate(generator, StreamerSettings.class, exampleFolder.resolve("streamer-config-schema.json"));
	}
	
	private static void generate(@NonNull SchemaGenerator generator, @NonNull Class<?> klazz, @NonNull Path outPath) throws IOException{
		log.info("Generating JSON schema for class {} in {}", klazz, outPath.toAbsolutePath());
		var jsonSchema = generator.generateSchema(klazz);
		Files.writeString(outPath, jsonSchema.toPrettyString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
