package fr.rakambda.channelpointsminer.miner.api.gql.integrity;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class IntegrityData{
	private String token;
	private Instant expiration;
	private String clientSessionId;
	private String clientVersion;
	private String xDeviceId;
}
