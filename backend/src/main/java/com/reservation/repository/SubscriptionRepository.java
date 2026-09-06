package com.reservation.repository;

import com.reservation.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(String userId);

    Optional<Subscription> findByUserIdAndTopicName(String userId, String topicName);

    boolean existsByUserIdAndTopicName(String userId, String topicName);
}
