package fr.raksrinana.channelpointsminer.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelEntity{
	private String id;
	private String username;
	private Instant lastStatusChange;
}
