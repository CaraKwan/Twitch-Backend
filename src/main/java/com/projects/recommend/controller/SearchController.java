package com.projects.recommend.controller;

import com.projects.recommend.entity.db.Item;
import com.projects.recommend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    private GameService gameService;

    @Autowired
    public SearchController(GameService gameService) {
        this.gameService = gameService;
    }

    //Return the video, stream and clip data for a game
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<Item>> search(@RequestParam(value = "game_id") String gameId) {
        return gameService.searchItems(gameId);
    }

}
