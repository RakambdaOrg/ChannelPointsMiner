package fr.rakambda.channelpointsminer.viewer.repository.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
