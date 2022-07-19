package com.projects.recommend.dao;

import com.projects.recommend.entity.db.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;


@Repository
public class RegisterDao {
    private SessionFactory sessionFactory;

    @Autowired
    public RegisterDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean register(User user) {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();  //Save to database when commit()
        } catch (PersistenceException | IllegalStateException ex) {  //user already be registered
            ex.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            if (session != null) session.close();
        }
        return true;
    }
}
