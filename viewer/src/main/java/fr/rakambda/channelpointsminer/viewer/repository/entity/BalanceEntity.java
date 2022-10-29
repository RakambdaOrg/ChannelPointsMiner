package fr.rakambda.channelpointsminer.viewer.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Balance")
public class BalanceEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;
    @Basic
    @Column(name = "ChannelID")
    private String channelId;
    @Basic
    @Column(name = "BalanceDate")
    private Instant balanceDate;
    @Basic
    @Column(name = "Balance")
    private int balance;
    @Basic
    @Column(name = "Reason")
    private String reason;
}
