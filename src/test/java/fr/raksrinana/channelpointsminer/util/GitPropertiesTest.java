package fr.raksrinana.channelpointsminer.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GitPropertiesTest{
	@Test
	void getVersion(){
		assertThat(GitProperties.getVersion()).isEqualTo("test-version");
	}
	
	@Test
	void getCommitId(){
		assertThat(GitProperties.getCommitId()).isEqualTo("abc123d");
	}
	
	@Test
	void getBranch(){
		assertThat(GitProperties.getBranch()).isEqualTo("test-branch");
	}
}