package com.projects.recommend.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.projects.recommend.entity.db.Item;
import com.projects.recommend.service.RecommendationException;
import com.projects.recommend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class RecommendationController {
    private RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    //Return a map of <ItemType, List of items> for recommendation
    //Recommend default contents for not log-in users
    //For log-in users, recommend based on their favorite items
    @RequestMapping(value = "/recommendation", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<Item>> recommendation(HttpServletRequest request) throws ServletException {
        HttpSession session = request.getSession(false);
        Map<String, List<Item>> itemMap;
        try {
            if (session == null) {
                itemMap = recommendationService.recommendItemsByDefault();
            } else {
                String userId = (String) request.getSession().getAttribute("user_id");
                itemMap = recommendationService.recommendItemsByUser(userId);
            }
        } catch (RecommendationException e) {
            throw new ServletException(e);
        }

        return itemMap;
    }


}
