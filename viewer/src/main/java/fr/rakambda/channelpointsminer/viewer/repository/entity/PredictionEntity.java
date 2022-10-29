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
