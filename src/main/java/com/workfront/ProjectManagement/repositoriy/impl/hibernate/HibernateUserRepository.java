package com.workfront.ProjectManagement.repositoriy.impl.hibernate;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.utilities.HibernateUtil;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "hibernate")
public class HibernateUserRepository implements UserRepository {
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
    public User getUserById(int id) {
        User user;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "from User where id =:id", User.class);
            query.setParameter("id", id);

            user = (User)((org.hibernate.query.Query) query).uniqueResult();

            if(user != null) {
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

    }

    @Override
    public void editUser(User user) {

    }

    @Override
    public void deleteUserById(int id) {

    }

    @Override
    public void updatePassword(int userId, String newPassword) {

    }

    @Override
    public int getUsersCount() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery( "select count(id) from User");
            return ((Long)((org.hibernate.query.Query) query).uniqueResult()).intValue();
        }
    }
}
