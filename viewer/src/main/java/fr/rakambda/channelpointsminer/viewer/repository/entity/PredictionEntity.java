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
@Table(name = "Prediction")
public class PredictionEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;
    @Basic
    @Column(name = "ChannelID")
    private String channelId;
    @Basic
    @Column(name = "EventID")
    private String eventId;
    @Basic
    @Column(name = "EventDate")
    private Instant eventDate;
    @Basic
    @Column(name = "Type")
    private String type;
    @Basic
    @Column(name = "Description")
    private String description;
}
