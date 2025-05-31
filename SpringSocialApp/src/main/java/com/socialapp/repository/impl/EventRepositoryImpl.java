package com.socialapp.repository.impl;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.EventNotification;
import com.socialapp.repository.EventRepository;
import org.hibernate.query.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class EventRepositoryImpl implements EventRepository {

    public static final int PAGE_SIZE = 5;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Event> getEvents(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Event> q = b.createQuery(Event.class);
        Root<Event> root = q.from(Event.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

           
            String title = params.get("title");
            if (title != null && !title.isEmpty()) {
                predicates.add(b.like(root.get("title"), String.format("%%%s%%", title)));
            }

           
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = s.createQuery(q);

       
        if (params != null && params.containsKey("page")) {
            int page = params != null && params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1;
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public List<Event> getAvailableEvents(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Event> q = b.createQuery(Event.class);
        Root<Event> root = q.from(Event.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

      
        Subquery<Long> subQuery = q.subquery(Long.class);
        Root<EventNotification> subRoot = subQuery.from(EventNotification.class);
        subQuery.select(b.count(subRoot)).where(b.equal(subRoot.get("event").get("event_id"), root.get("event_id")));
        predicates.add(b.equal(subQuery, 0L));

       
        if (params != null) {
            String title = params.get("title");
            if (title != null && !title.isEmpty()) {
                predicates.add(b.like(root.get("title"), "%" + title + "%"));
            }

            String eventDate = params.get("eventDate");
            if (eventDate != null && !eventDate.isEmpty()) {
                predicates.add(b.equal(root.get("start_date"), eventDate));
            }
        }

        q.where(predicates.toArray(new Predicate[0]));

        Query query = s.createQuery(q);

       
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public Event getEventById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Event.class, id);
    }

    @Override
    public Event addOrUpdateEvent(Event event) {
        Session s = this.factory.getObject().getCurrentSession();
        if (event.getEvent_id() == null) {
            s.persist(event); 
        } else {
            s.merge(event); 
        }
        return event;
    }

    @Override
    public void deleteEvent(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Event event = this.getEventById(id);
        if (event != null) {
            s.delete(event);
        }
    }

    @Override
    public long countEvent() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(e.event_id) FROM Event e", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count : 0;
    }
}
