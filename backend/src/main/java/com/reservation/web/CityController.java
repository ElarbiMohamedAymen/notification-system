package com.reservation.web;

import com.reservation.dto.SlotDto;
import com.reservation.dto.SubscribeRequest;
import com.reservation.service.SlotService;
import com.reservation.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final SubscriptionService subscriptionService;
    private final SlotService slotService;

    public CityController(SubscriptionService subscriptionService, SlotService slotService) {
        this.subscriptionService = subscriptionService;
        this.slotService = slotService;
    }

    @PostMapping("/{cityName}/subscribe")
    public ResponseEntity<Void> subscribe(@PathVariable String cityName, @Valid @RequestBody SubscribeRequest request) {
        subscriptionService.subscribe(request.userId(), cityName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cityName}/subscribe")
    public ResponseEntity<Void> unsubscribe(@PathVariable String cityName, @RequestParam String userId) {
        subscriptionService.unsubscribe(userId, cityName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public List<String> mySubscriptions(@RequestParam String userId) {
        return subscriptionService.citiesFor(userId);
    }

    @GetMapping("/{cityName}/slots")
    public List<SlotDto> availableSlots(@PathVariable String cityName) {
        return slotService.availableSlots(cityName);
    }
}
