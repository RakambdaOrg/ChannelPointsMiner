package fr.rakambda.channelpointsminer.miner.util.json;

import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import tools.jackson.databind.ValueDeserializer;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class ColorDeserializerTest extends DeserializerTest<Color>{
	@Test
	void hexValue(){
		assertThat(deserialize("\"%s\"".formatted("#FF0000"))).isEqualTo(Color.RED);
	}
	
	@Test
	void decValue(){
		assertThat(deserialize("%d".formatted(0xFF0000))).isEqualTo(Color.RED);
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected ValueDeserializer<Color> getDeserializer(){
		return new ColorDeserializer();
	}
}