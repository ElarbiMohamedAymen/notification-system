package com.reservation.service;

import com.reservation.domain.Slot;
import com.reservation.domain.SlotStatus;
import com.reservation.domain.Topic;
import com.reservation.dto.CreateSlotRequest;
import com.reservation.dto.SlotDto;
import com.reservation.repository.SlotRepository;
import com.reservation.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SlotService {

    private final SlotRepository slotRepository;
    private final TopicRepository topicRepository;
    private final NotificationService notificationService;

    public SlotService(SlotRepository slotRepository, TopicRepository topicRepository,
                        NotificationService notificationService) {
        this.slotRepository = slotRepository;
        this.topicRepository = topicRepository;
        this.notificationService = notificationService;
    }

    public SlotDto publishSlot(CreateSlotRequest request) {
        Topic topic = topicRepository.findByName(request.topicName())
                .orElseGet(() -> topicRepository.save(new Topic(request.topicName())));

        Slot slot = new Slot(topic, request.startTime(), request.endTime());
        slot = slotRepository.save(slot);

        SlotDto dto = SlotDto.from(slot);
        // Only subscribers of this topic receive the push - everyone else's
        // client stays idle instead of polling for availability.
        notificationService.slotPublished(dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<SlotDto> availableSlots(String topicName) {
        return slotRepository.findByTopicNameAndStatus(topicName, SlotStatus.AVAILABLE).stream()
                .map(SlotDto::from)
                .toList();
    }
}
