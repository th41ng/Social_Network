
package com.socialapp.repository;

import com.socialapp.pojo.Event;
import java.util.List;
import java.util.Map;


public interface EventRepository {

    List<Event> getEvents(Map<String, String> params);

    List<Event> getAvailableEvents(Map<String, String> params);

    Event getEventById(int id);

    Event addOrUpdateEvent(Event event);

    void deleteEvent(int id);

    long countEvent();
}
