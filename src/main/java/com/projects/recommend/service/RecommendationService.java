package com.projects.recommend.service;

import com.projects.recommend.dao.FavoriteDao;
import com.projects.recommend.entity.db.Item;
import com.projects.recommend.entity.db.ItemType;
import com.projects.recommend.entity.response.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class RecommendationService {
    private static final int DEFAULT_GAME_LIMIT = 3;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;
    private GameService gameService;
    private FavoriteDao favoriteDao;

    @Autowired
    public RecommendationService(GameService gameService, FavoriteDao favoriteDao) {
        this.gameService = gameService;
        this.favoriteDao = favoriteDao;
    }

    // Return a list of items base on the given type for the given games
    private List<Item> recommendByFavoriteHistory(Set<String> favoritedItemIds, List<String> favoriteGameIds, ItemType type) throws RecommendationException {
        // Count the favorite game IDs from the database for the given user. key: gameId, value: favorite times
        Map<String, Long> favoriteGameIdByCount = new HashMap<>();
        for(String gameId : favoriteGameIds) {
            favoriteGameIdByCount.put(gameId, favoriteGameIdByCount.getOrDefault(gameId, 0L) + 1);
        }

        // Sort the gameIds by count in descending order.
        List<Map.Entry<String, Long>> sortedFavoriteGameIdListByCount = new ArrayList<>(favoriteGameIdByCount.entrySet());
        sortedFavoriteGameIdListByCount.sort((Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) -> Long.compare(e2.getValue(), e1.getValue()));

        if (sortedFavoriteGameIdListByCount.size() > DEFAULT_GAME_LIMIT) {
            sortedFavoriteGameIdListByCount = sortedFavoriteGameIdListByCount.subList(0, DEFAULT_GAME_LIMIT);
        }

        List<Item> recommendedItems = new ArrayList<>();

        // Search Twitch API based on the favorite gameIds from the previous step.
        for (Map.Entry<String, Long> favoriteGame : sortedFavoriteGameIdListByCount) {
            List<Item> items = null;
            try {
                items = gameService.searchByType(favoriteGame.getKey(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }

            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                if (!favoritedItemIds.contains(item.getId())) {
                    recommendedItems.add(item);
                }
            }
        }
        return recommendedItems;
    }

    // Return a <ItemType, List of items> map for recommendation for a given user.
    // Each item is recommended based on the favorite items of this user.
    public Map<String, List<Item>> recommendItemsByUser(String userId) throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        Set<String> favoriteItemIds = favoriteDao.getFavoriteItemIds(userId);
        Map<String, List<String>> favoriteGameIds = favoriteDao.getFavoriteGameIds(favoriteItemIds);

        //if user didn't favorite items for a certain itemType, recommend item for this type based on top games
        //else, recommend based on their favorite history
        for (Map.Entry<String, List<String>> entry : favoriteGameIds.entrySet()) {
            if (entry.getValue().size() == 0) {
                List<Game> topGames;
                try {
                    topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
                } catch (TwitchException e) {
                    throw new RecommendationException("Failed to get game data for recommendation");
                }
                recommendedItemMap.put(entry.getKey(), recommendByTopGames(ItemType.valueOf(entry.getKey()), topGames));
            } else {
                recommendedItemMap.put(entry.getKey(), recommendByFavoriteHistory(favoriteItemIds, entry.getValue(), ItemType.valueOf(entry.getKey())));
            }
        }
        return recommendedItemMap;
    }


    // Return a list of item base on the given type by top games.
    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();

        for (Game game : topGames) {
            List<Item> items = null;
            try {
                items = gameService.searchByType(game.getId(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommendation result");
            }
            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    return recommendedItems;
                }
                recommendedItems.add(item);
            }
        }
        return recommendedItems;
    }

    // Return a <ItemType, List of items> map for recommendation by default.
    // Each item is a recommended based on the top games from Twitch API.
    public Map<String, List<Item>> recommendItemsByDefault() throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        List<Game> topGames = null;
        try {
            topGames = gameService.topGames(DEFAULT_GAME_LIMIT);
        } catch (TwitchException e) {
            throw new RecommendationException("Failed to get game data for recommendation");
        }

        //for each type, get a list of recommended items
        for (ItemType type : ItemType.values()) {
            recommendedItemMap.put(type.toString(), recommendByTopGames(type, topGames));
        }
        return recommendedItemMap;
    }


}
