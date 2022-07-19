package com.projects.recommend.entity.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.recommend.entity.db.Item;

//Used to parse and match favorite data from frontend, key:"favorite", value: Item
public class FavoriteRequestBody {
    private final Item favoriteItem;

    @JsonCreator
    public FavoriteRequestBody(@JsonProperty("favorite") Item favoriteItem) {
        this.favoriteItem = favoriteItem;
    }

    public Item getFavoriteItem() {
        return favoriteItem;
    }
}

