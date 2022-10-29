package fr.rakambda.channelpointsminer.miner.database.model.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrediction{
    
    @NotNull
    private String channelId;
    private int userId;
    @NotNull
    private String badge;
}
