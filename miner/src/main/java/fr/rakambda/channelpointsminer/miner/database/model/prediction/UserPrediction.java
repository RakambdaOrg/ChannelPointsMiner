package fr.rakambda.channelpointsminer.miner.database.model.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrediction{
    
    @NonNull
    private String channelId;
    private int userId;
    @NonNull
    private String badge;
}
