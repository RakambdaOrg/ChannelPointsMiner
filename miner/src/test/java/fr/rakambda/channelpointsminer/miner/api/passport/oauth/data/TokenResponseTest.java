package fr.rakambda.channelpointsminer.miner.api.passport.oauth.data;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseTest{
	@Test
	void authorizationPending(){
		assertThat(new TokenResponse(null, null, null, null, null, "authorization_pending").isAuthorizationPending()).isTrue();
		assertThat(new TokenResponse(null, null, null, null, null, "aaaa").isAuthorizationPending()).isFalse();
	}
}