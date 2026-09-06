package com.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "slots")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    private String reservedBy;

    private Instant reservedAt;

    // Guards concurrent reservation attempts: two users racing on the same
    // slot will conflict here instead of both succeeding.
    @Version
    private Long version;

    protected Slot() {
    }

    public Slot(Topic topic, Instant startTime, Instant endTime) {
        this.topic = topic;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void reserveFor(String userId) {
        if (status != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot " + id + " is not available");
        }
        this.status = SlotStatus.RESERVED;
        this.reservedBy = userId;
        this.reservedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public Instant getReservedAt() {
        return reservedAt;
    }

    public Long getVersion() {
        return version;
    }
}
