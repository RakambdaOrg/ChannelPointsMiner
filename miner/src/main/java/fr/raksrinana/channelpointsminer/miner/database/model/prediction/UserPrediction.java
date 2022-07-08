package fr.raksrinana.channelpointsminer.miner.database.model.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrediction{
    
    @NotNull
    private String channelId;
    private int userId;
    @Nullable
    private String resolvedPredictionId;
    @NotNull
    private String badge;
}
