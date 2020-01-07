package com.workfront.ProjectManagement.repositoriy.impl.hibernate;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.utilities.HibernateUtil;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "hibernate")
public class HibernateActionStatusRepository implements ActionStatusRepository {
    @Override
    public List<ActionStatus> getActionStatuses() {

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from ActionStatus order by id", ActionStatus.class).list();
        }
    }
}
