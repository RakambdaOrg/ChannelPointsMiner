package fr.raksrinana.channelpointsminer.viewer.api;

import fr.raksrinana.channelpointsminer.viewer.api.data.BalanceData;
import fr.raksrinana.channelpointsminer.viewer.repository.BalanceRepository;
import fr.raksrinana.channelpointsminer.viewer.repository.entity.BalanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Collection;

@Service
public class BalanceService{
	private final BalanceRepository balanceRepository;
	
	@Autowired
	public BalanceService(BalanceRepository balanceRepository){this.balanceRepository = balanceRepository;}
	
	public Collection<BalanceData> getAllBalance(String channelId){
		return balanceRepository.findAllByChannelId(channelId).stream()
				.map(this::entityToData)
				.toList();
	}
	
	public Collection<BalanceData> getRangeBalance(String channelId, Instant start, Instant end){
		return balanceRepository.findAllByChannelIdAndBalanceDateBetween(channelId, start, end).stream()
				.map(this::entityToData)
				.toList();
	}
	
	private BalanceData entityToData(BalanceEntity entity){
		return BalanceData.builder()
				.date(entity.getBalanceDate())
				.balance(entity.getBalance())
				.reason(entity.getReason())
				.build();
	}
}
