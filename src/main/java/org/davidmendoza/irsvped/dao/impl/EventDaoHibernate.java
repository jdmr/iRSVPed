/*
 * The MIT License
 *
 * Copyright 2014 J. David Mendoza <jdmendozar@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.irsvped.dao.impl;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.irsvped.dao.BaseDao;
import org.davidmendoza.irsvped.dao.EventDao;
import org.davidmendoza.irsvped.model.Event;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Repository
@Transactional
public class EventDaoHibernate extends BaseDao implements EventDao {

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(Map<String, Object> params) {
        log.debug("Event list");
        Criteria criteria = currentSession().createCriteria(Event.class);
        Criteria countCriteria = currentSession().createCriteria(Event.class);
        if (params.containsKey("filter")) {
            String filter = (String) params.get("filter");
            criteria.createAlias("user", "user_alias");
            countCriteria.createAlias("user", "user_alias");
            Disjunction properties = Restrictions.disjunction();
            properties.add(Restrictions.ilike("code", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("name", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("description", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("street", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("city", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("zip", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("hostName", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("hostPhone", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("hostEmail", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("user_alias.username", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("user_alias.firstName", filter, MatchMode.ANYWHERE));
            properties.add(Restrictions.ilike("user_alias.lastName", filter, MatchMode.ANYWHERE));
            criteria.add(properties);
            countCriteria.add(properties);
        }
        if (params.containsKey("mine")) {
            String principal = (String) params.get("principal");
            Criteria a = criteria.createCriteria("user");
            a.add(Restrictions.eq("username", principal));
        }
        if (params.containsKey("max")) {
            criteria.setMaxResults((Integer) params.get("max"));
            if (params.containsKey("offset")) {
                criteria.setFirstResult((Integer) params.get("offset"));
            }
        }
        if (params.containsKey("sort")) {
            if (((String) params.get("order")).equals("desc")) {
                criteria.addOrder(Order.desc((String) params.get("sort")));
                params.put("order", "asc");
            } else {
                criteria.addOrder(Order.asc((String) params.get("sort")));
                params.put("order", "desc");
            }
        }
        params.put("list", criteria.list());
        countCriteria.setProjection(Projections.rowCount());
        params.put("totalItems", countCriteria.list().get(0));
        return params;

    }

    @Override
    public Event create(Event event) {
        log.debug("Creating event {}", event.getId());
        currentSession().save(event);
        return event;
    }

    @Transactional(readOnly = true)
    @Override
    public Event get(String eventId) {
        return (Event) currentSession().get(Event.class, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public Event getByCode(String code) {
        Query query = currentSession().getNamedQuery("findEventByCode");
        query.setString("code", code);
        return (Event) query.uniqueResult();
    }

    @Override
    public void delete(Event event) {
        Query query = currentSession().createQuery("delete from Party p where p.event.id = :eventId");
        query.setString("eventId", event.getId());
        query.executeUpdate();
        currentSession().delete(event);
    }

    @Override
    public Event update(Event event) {
        log.debug("Updating event {}", event.getId());
        Event e = (Event) currentSession().get(Event.class, event.getId());
        log.debug("Found event {}", e);
        if (StringUtils.isBlank(event.getImageName())) {
            BeanUtils.copyProperties(event, e, new String[]{"id", "dateCreated", "contentType", "imageName", "imageSize", "imageData"});
        } else {
            BeanUtils.copyProperties(event, e, new String[]{"id", "dateCreated"});
        }
        currentSession().update(e);
        return e;
    }

}
