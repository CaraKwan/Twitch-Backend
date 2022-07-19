package com.projects.recommend.service;

import com.projects.recommend.dao.FavoriteDao;
import com.projects.recommend.entity.db.Item;
import com.projects.recommend.entity.db.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavoriteService {
    private FavoriteDao favoriteDao;

    @Autowired
    public FavoriteService(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }

    public void setFavoriteItem(String userId, Item item) {
        favoriteDao.setFavoriteItem(userId, item);
    }

    public void unsetFavoriteItem(String userId, String itemId) {
        favoriteDao.unsetFavoriteItem(userId, itemId);
    }

    //Return a map of ItemType - Item list
    public Map<String, List<Item>> getFavoriteItems(String userId) {
        Map<String, List<Item>> itemMap = new HashMap<>();
        for (ItemType itemType : ItemType.values()) {
            itemMap.put(itemType.toString(), new ArrayList<>());
        }

        Set<Item> favoriteItems = favoriteDao.getFavoriteItems(userId);
        for (Item item : favoriteItems) {
            itemMap.get(item.getType().toString()).add(item);
        }
        return itemMap;
    }
}
