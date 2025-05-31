

package com.socialapp.service;

import com.socialapp.pojo.Event;
import java.util.List;
import java.util.Map;


public interface EventService {

    List<Event> getEvents(Map<String, String> params);

    List<Event> getAvailableEvents(Map<String, String> params);

    Event getEventById(int id);

    Event addOrUpdateEvent(Event event);

    long countEvent();

    void deleteEvent(int id);

}
