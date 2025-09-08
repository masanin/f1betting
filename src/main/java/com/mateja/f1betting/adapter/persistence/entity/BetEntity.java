package com.mateja.f1betting.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bets")
public class BetEntity {
    @Id
    @Column("bet_id")
    private String betId;

    @Column("user_id")
    private String userId;

    @Column("event_id")
    private int eventId;

    @Column("driver_id")
    private int driverId;

    @Column("amount")
    private BigDecimal amount;

    @Column("odds")
    private BigDecimal odds;

    @Column("status")
    private String status;

    @Column("potential_winnings")
    private BigDecimal potentialWinnings;

    @Column("placed_at")
    private LocalDateTime placedAt;
}
