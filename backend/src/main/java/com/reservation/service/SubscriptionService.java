package com.reservation.service;

import com.reservation.domain.Subscription;
import com.reservation.domain.Topic;
import com.reservation.repository.SubscriptionRepository;
import com.reservation.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, TopicRepository topicRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
    }

    public void subscribe(String userId, String topicName) {
        if (subscriptionRepository.existsByUserIdAndTopicName(userId, topicName)) {
            return;
        }
        Topic topic = topicRepository.findByName(topicName)
                .orElseGet(() -> topicRepository.save(new Topic(topicName)));
        subscriptionRepository.save(new Subscription(userId, topic));
    }

    public void unsubscribe(String userId, String topicName) {
        subscriptionRepository.findByUserIdAndTopicName(userId, topicName)
                .ifPresent(subscriptionRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<String> topicsFor(String userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(subscription -> subscription.getTopic().getName())
                .toList();
    }
}
