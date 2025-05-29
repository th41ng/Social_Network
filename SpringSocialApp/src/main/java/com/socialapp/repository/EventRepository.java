/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.Event;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface EventRepository {
    List<Event> getEvents(Map<String, String> params);
    List<Event> getAvailableEvents(Map<String, String> params);
    Event getEventById(int id);
    Event addOrUpdateEvent(Event event);
    void deleteEvent(int id);
    long countEvent();
}
