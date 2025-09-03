package fr.rakambda.channelpointsminer.miner.database.converter;

import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.database.model.prediction.UserPrediction;
import org.jspecify.annotations.NonNull;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Converters{
	@NonNull
	public static UserPrediction convertUserPrediction(@NonNull ResultSet rs) throws SQLException{
		return UserPrediction.builder()
				.userId(rs.getInt("UserID"))
				.channelId(rs.getString("ChannelID"))
				.badge(rs.getString("Badge"))
				.build();
	}
	
	@NonNull
	public static OutcomeStatistic convertOutcomeTrust(@NonNull ResultSet rs) throws SQLException{
		return OutcomeStatistic.builder()
				.badge(rs.getString("Badge"))
				.userCnt(rs.getInt("UserCnt"))
				.averageWinRate(rs.getDouble("AvgWinRate"))
				.averageUserBetsPlaced(rs.getDouble("AvgUserBetsPlaced"))
				.averageUserWins(rs.getDouble("AvgUserWins"))
				.averageReturnOnInvestment(rs.getDouble("AvgReturnOnInvestment"))
				.build();
	}
}
