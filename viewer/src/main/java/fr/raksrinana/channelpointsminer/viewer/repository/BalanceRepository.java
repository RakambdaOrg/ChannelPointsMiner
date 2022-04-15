package fr.raksrinana.channelpointsminer.viewer.repository;

import fr.raksrinana.channelpointsminer.viewer.repository.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Integer>{
    List<BalanceEntity> findAllByChannelId(String channelId);
}
