package com.projects.recommend.service;
import com.projects.recommend.dao.LoginDao;
import com.projects.recommend.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class LoginService {
    private LoginDao loginDao;

    @Autowired
    public LoginService(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    //Verify user login
    public String verifyLogin(String userId, String password) throws IOException {
        password = Util.encryptPassword(userId, password);
        return loginDao.verifyLogin(userId, password);
    }
}
