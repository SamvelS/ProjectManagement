package com.workfront.ProjectManagement.repositoriy.impl.hibernate;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import com.workfront.ProjectManagement.utilities.Constants;
import com.workfront.ProjectManagement.utilities.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "hibernate")
public class HibernateProjectRepository implements ProjectRepository {
    @Override
    public List<Project> getProjects(int from, int count) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from Project order by id ", Project.class);
            query.setFirstResult(from);
            query.setMaxResults(count);

            return query.getResultList();
        }
    }

    @Override
    public Project getProjectById(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from Project where id =:id", Project.class);
            query.setParameter("id", id);
            return (Project)((org.hibernate.query.Query) query).uniqueResult();
        }
    }

    @Override
    public List<Project> getProjectsByStatusId(int statusId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String queryText = "from Project";
            if(statusId != Constants.getAllStatusesId()) {
                queryText += " where status.id =:statusId";
            }
            queryText += " order by id";

            Query query = session.createQuery( queryText, Project.class);
            if(statusId != Constants.getAllStatusesId()) {
                query.setParameter("statusId", statusId);
            }

            return query.getResultList();
        }
    }

    @Override
    public void createProject(Project project) {
        project.setCreatedOn(new Date());
        project.setStatus(new ActionStatus());
        project.getStatus().setId(1);

        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.save(project);

            tx.commit();
        }
        catch(RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
        finally {
            if(session != null) {
                session.close();
            }
        }
    }

    @Override
    public void editProject(Project project) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.update(project);

            tx.commit();
        }
        catch(RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
        finally {
            if(session != null) {
                session.close();
            }
        }
    }

    @Override
    public void deleteProjectById(int id) {
        //TODO: add after Task repository is done
    }

    @Override
    public int getProjectsCount() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "select count(id) from Project ");
            return ((Long)((org.hibernate.query.Query) query).uniqueResult()).intValue();
        }
    }
}
