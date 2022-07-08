package fr.raksrinana.channelpointsminer.miner.database.converter;

import fr.raksrinana.channelpointsminer.miner.database.model.prediction.UserPrediction;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Converters{
    
    public static UserPrediction convertUserPrediction(ResultSet rs) throws SQLException{
        return UserPrediction.builder()
                .userId(rs.getInt("UserID"))
                .channelId(rs.getString("ChannelID"))
                .resolvedPredictionId(rs.getString("ResolvedPredictionID"))
                .badge(rs.getString("Badge"))
                .build();
    }
}
