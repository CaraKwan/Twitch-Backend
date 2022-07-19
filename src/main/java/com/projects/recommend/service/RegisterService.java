package com.projects.recommend.service;

import com.projects.recommend.dao.RegisterDao;
import com.projects.recommend.entity.db.User;
import com.projects.recommend.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
public class RegisterService {
    private RegisterDao registerDao;

    @Autowired
    public RegisterService(RegisterDao registerDao) {
        this.registerDao = registerDao;
    }

    public boolean register(User user)  throws IOException {
        user.setPassword(Util.encryptPassword(user.getUserId(), user.getPassword()));
        return registerDao.register(user);
    }

}
