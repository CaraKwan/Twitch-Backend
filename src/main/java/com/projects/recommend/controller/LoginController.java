package com.projects.recommend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.recommend.entity.request.LoginRequestBody;
import com.projects.recommend.entity.response.LoginResponseBody;
import com.projects.recommend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {
    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    //Handle user log in
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody LoginRequestBody requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = loginService.verifyLogin(requestBody.getUserId(), requestBody.getPassword());

        // Create a new session for the user if login is verified, otherwise return unauthorized error.
        if (!firstName.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("user_id", requestBody.getUserId());
            session.setMaxInactiveInterval(600);  //expiration time : 600 seconds

            LoginResponseBody loginResponseBody = new LoginResponseBody(requestBody.getUserId(), firstName);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(loginResponseBody));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
