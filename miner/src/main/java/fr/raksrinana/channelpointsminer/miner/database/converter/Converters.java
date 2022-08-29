package fr.raksrinana.channelpointsminer.miner.database.converter;

import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.raksrinana.channelpointsminer.miner.database.model.prediction.UserPrediction;
import org.jetbrains.annotations.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Converters{
	@NotNull
	public static UserPrediction convertUserPrediction(@NotNull ResultSet rs) throws SQLException{
		return UserPrediction.builder()
				.userId(rs.getInt("UserID"))
				.channelId(rs.getString("ChannelID"))
				.badge(rs.getString("Badge"))
				.build();
	}
	
	@NotNull
	public static OutcomeStatistic convertOutcomeTrust(@NotNull ResultSet rs) throws SQLException{
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
