package com.workfront.ProjectManagement.repositoriy.impl.hibernate;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.domain.UserStatus;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.utilities.Beans;
import com.workfront.ProjectManagement.utilities.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "hibernate")
public class HibernateUserRepository implements UserRepository {
    @Autowired
    private Beans beans;

    @Override
    public User getUserByEmail(String email, boolean getPassword) {
        User user;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from User where email =:email", User.class);
            query.setParameter("email", email);

            user = (User)((org.hibernate.query.Query) query).uniqueResult();

            if(user != null && !getPassword) {
                user.setPassword("");
            }
        }
        return user;
    }

    @Override
    public User getUserById(int id, boolean getPassword) {
        User user;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from User where id =:id", User.class);
            query.setParameter("id", id);

            user = (User)((org.hibernate.query.Query) query).uniqueResult();

            if(user != null && !getPassword) {
                user.setPassword("");
            }
        }
        return user;
    }

    @Override
    public List<User> getUsers(int from, int count) {
        List<User> users = Collections.emptyList();
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from User order by id ", User.class);
            query.setFirstResult(from);
            query.setMaxResults(count);

            users = query.getResultList();

            users.forEach(u -> u.setPassword(""));
        }

        return users;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = Collections.emptyList();
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            users = session.createQuery( "from User order by firstName, lastName", User.class).list();

            users.forEach(u -> u.setPassword(""));
        }

        return users;
    }

    @Override
    public void createUser(User user) {
        user.setPassword(this.beans.passwordEncoder().encode(user.getPassword()));
        user.setStatusId(UserStatus.CHANGE_PASSWORD.getValue());
        user.setCreatedOn(new Date());

        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.save(user);

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
    public void editUser(User user) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            User originalUser = this.getUserById(user.getId(), true);

            user.setPassword(originalUser.getPassword());
            user.setStatusId(originalUser.getStatusId());

            session.update(user);

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
    public void deleteUserById(int id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // TODO: add after Task repository is ready

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
    public void updatePassword(int userId, String newPassword) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);

            if(user != null) {
                user.setPassword(this.beans.passwordEncoder().encode(newPassword));
                user.setStatusId(UserStatus.ACTIVE_USER.getValue());

                session.beginTransaction();
                session.update(user);
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public int getUsersCount() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "select count(id) from User");
            return ((Long)((org.hibernate.query.Query) query).uniqueResult()).intValue();
        }
    }
}
