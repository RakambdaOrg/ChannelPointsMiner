package fr.raksrinana.channelpointsminer.viewer.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceData{
    private Instant date;
    private int balance;
	private String reason;
}
