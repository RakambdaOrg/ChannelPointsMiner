package fr.raksrinana.channelpointsminer.miner.api.gql.data;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GQLResponseTest{
	@Mock
	private GQLError gqlError;
	
	@Test
	void isErrorBecauseError(){
		var tested = GQLResponse.builder().error("error").build();
		assertThat(tested.isError()).isTrue();
	}
	
	@Test
	void isErrorBecauseErrors(){
		var tested = GQLResponse.builder().errors(List.of(gqlError)).build();
		assertThat(tested.isError()).isTrue();
	}
	
	@Test
	void isNotError(){
		var tested = GQLResponse.builder().build();
		assertThat(tested.isError()).isFalse();
	}
}