package com.reservation.service;

import com.reservation.domain.City;
import com.reservation.domain.Subscription;
import com.reservation.repository.CityRepository;
import com.reservation.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, CityRepository cityRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.cityRepository = cityRepository;
    }

    public void subscribe(String userId, String cityName) {
        if (subscriptionRepository.existsByUserIdAndCityName(userId, cityName)) {
            return;
        }
        City city = cityRepository.findByName(cityName)
                .orElseGet(() -> cityRepository.save(new City(cityName)));
        subscriptionRepository.save(new Subscription(userId, city));
    }

    public void unsubscribe(String userId, String cityName) {
        subscriptionRepository.findByUserIdAndCityName(userId, cityName)
                .ifPresent(subscriptionRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<String> citiesFor(String userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(subscription -> subscription.getCity().getName())
                .toList();
    }
}
