package com.reservation.repository;

import com.reservation.domain.Slot;
import com.reservation.domain.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByTopicNameAndStatus(String topicName, SlotStatus status);
}
