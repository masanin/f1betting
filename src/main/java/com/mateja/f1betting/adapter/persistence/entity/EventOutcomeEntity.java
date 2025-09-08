package com.mateja.f1betting.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_outcomes")
public class EventOutcomeEntity {
    @Id
    @Column("event_id")
    private Integer eventId;

    @Column("winner_driver_id")
    private Integer winnerDriverId;
}
