package com.socialapp.service.impl;

import com.socialapp.pojo.Event;
import com.socialapp.repository.EventRepository;
import com.socialapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> getEvents(Map<String, String> params) {
        return eventRepository.getEvents(params);
    }

    @Override
    public Event getEventById(int id) {
        return eventRepository.getEventById(id);
    }

    @Override
    public Event addOrUpdateEvent(Event event) {
        return eventRepository.addOrUpdateEvent(event);
    }

    @Override
    public void deleteEvent(int id) {
        eventRepository.deleteEvent(id);
    }

    @Override
    public List<Event> getAvailableEvents(Map<String, String> params) {
        return eventRepository.getAvailableEvents(params);
    }

     @Override
    public long countEvent() {
        return this.eventRepository.countEvent();
    }
}
