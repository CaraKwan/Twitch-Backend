package com.projects.recommend.controller;

import com.projects.recommend.entity.db.Item;
import com.projects.recommend.entity.request.FavoriteRequestBody;
import com.projects.recommend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class FavoriteController {
    private FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    //Handle user add favorite item
    @RequestMapping(value = "/favorite", method = RequestMethod.POST)
    public void setFavoriteItem(@RequestBody FavoriteRequestBody requestBody, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        //Only logged-in users can add favorite items, otherwise return error
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ;
        }
        String userId = (String) session.getAttribute("user_id");
        favoriteService.setFavoriteItem(userId, requestBody.getFavoriteItem());
    }


    //Handle user remove favorite item
    @RequestMapping(value = "/favorite", method = RequestMethod.DELETE)
    public void unsetFavoriteItem(@RequestBody FavoriteRequestBody requestBody, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        //Only logged-in users can remove favorite items, otherwise return error
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String userId = (String) session.getAttribute("user_id");
        favoriteService.unsetFavoriteItem(userId, requestBody.getFavoriteItem().getId());
    }


    //Get user favorite item list
    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<Item>> getFavoriteItem(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        //Only logged-in users can access favorite items, otherwise return error
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new HashMap<>();
        }
        String userId = (String) session.getAttribute("user_id");
        return favoriteService.getFavoriteItems(userId);
    }

}
