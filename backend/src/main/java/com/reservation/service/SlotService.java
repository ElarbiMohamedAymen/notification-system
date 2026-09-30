package com.reservation.service;

import com.reservation.domain.City;
import com.reservation.domain.Slot;
import com.reservation.domain.SlotStatus;
import com.reservation.dto.CreateSlotRequest;
import com.reservation.dto.SlotDto;
import com.reservation.event.SlotEventProducer;
import com.reservation.repository.CityRepository;
import com.reservation.repository.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SlotService {

    private final SlotRepository slotRepository;
    private final CityRepository cityRepository;
    private final SlotEventProducer slotEventProducer;

    public SlotService(SlotRepository slotRepository, CityRepository cityRepository,
                        SlotEventProducer slotEventProducer) {
        this.slotRepository = slotRepository;
        this.cityRepository = cityRepository;
        this.slotEventProducer = slotEventProducer;
    }

    public SlotDto publishSlot(CreateSlotRequest request) {
        City city = cityRepository.findByName(request.cityName())
                .orElseGet(() -> cityRepository.save(new City(request.cityName())));

        Slot slot = new Slot(city, request.startTime(), request.endTime());
        slot = slotRepository.save(slot);

        SlotDto dto = SlotDto.from(slot);
        // Published to RabbitMQ rather than pushed directly: every app
        // instance consumes it and rebroadcasts to its own subscribers, so
        // only users listening for this city ever see it - no polling.
        slotEventProducer.slotPublished(dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<SlotDto> availableSlots(String cityName) {
        return slotRepository.findByCityNameAndStatus(cityName, SlotStatus.AVAILABLE).stream()
                .map(SlotDto::from)
                .toList();
    }
}
