package com.projects.recommend.dao;
import com.projects.recommend.entity.db.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDao {
    private SessionFactory sessionFactory;

    @Autowired
    public LoginDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Check the given userId and password against the database data. Returns the userName if it's correct
    public String verifyLogin(String userId, String password) {
        String name = "";
        Session session = null;
        try  {
            session = sessionFactory.openSession();  //Connect to database
            User user = session.get(User.class, userId);
            if(user != null && user.getPassword().equals((password))) {
                name = user.getFirstName();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return name;
    }

}
