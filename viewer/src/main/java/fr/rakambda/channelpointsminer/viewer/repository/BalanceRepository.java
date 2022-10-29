package fr.rakambda.channelpointsminer.viewer.repository;

import fr.rakambda.channelpointsminer.viewer.repository.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Integer>{
	List<BalanceEntity> findAllByChannelId(String channelId);
	
	List<BalanceEntity> findAllByChannelIdAndBalanceDateBetween(String channelId, Instant start, Instant end);
}
