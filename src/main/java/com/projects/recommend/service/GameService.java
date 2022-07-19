package com.projects.recommend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.recommend.entity.db.Item;
import com.projects.recommend.entity.db.ItemType;
import com.projects.recommend.entity.response.Game;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class GameService {
    private static final String TOKEN = "Bearer bzcu1lnhhkwa5oepf8e67tt955a9k1";
    private static final String CLIENT_ID = "17fhus2kbv4ohst0fl09gfog7oghx2";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;
    private static final String STREAM_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/streams?game_id=%s&first=%s";
    private static final String VIDEO_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/videos?game_id=%s&first=%s";
    private static final String CLIP_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/clips?game_id=%s&first=%s";
    private static final String TWITCH_BASE_URL = "https://www.twitch.tv/";
    private static final int DEFAULT_SEARCH_LIMIT = 20;

    // Build the request URL to call Twitch APIs, return different urls for top games and search games
    //e.g. https://api.twitch.tv/helix/games/top
    private String buildGameURL(String url, String gameName, int limit) {
        if (gameName.equals("")) {
            return String.format(url, limit);
        } else {
            try {
                // Encode gameName in case of space
                gameName = URLEncoder.encode(gameName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return String.format(url, gameName);
        }
    }

    //Build the request URL to call Twitch APIs
    //e.g. https://api.twitch.tv/helix/clips?game_id=12924.
    private String buildSearchURL(String url, String gameId, int limit) {
        try {
            gameId = URLEncoder.encode(gameId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(url, gameId, limit);
    }

    // Call Twitch API with the given URL, and return the response body
    private String searchTwitch(String url) throws TwitchException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Define a response handler to parse and return response body
        ResponseHandler<String> responseHandler = response -> {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                System.out.println("Response status: " + response.getStatusLine().getReasonPhrase());
                throw new TwitchException("Failed to get result from Twitch API");
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new TwitchException("Failed to get result from Twitch API");
            }
            JSONObject obj = new JSONObject(EntityUtils.toString(entity));
            return obj.getJSONArray("data").toString();
        };

        // Make http request and return response parsed with responseHandler
        try {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);
            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Convert the json data returned from calling twitch API to an Arraylist of Game objects
    private List<Game> getGameList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse game data from Twitch API");
        }
    }

    // Return a list of top games
    public List<Game> topGames(int limit) throws TwitchException {
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        String url = buildGameURL(TOP_GAME_URL, "", limit);
        return getGameList(searchTwitch(url));
    }

    // Return a certain game
    public Game searchGame(String gameName) throws TwitchException {
        String url = buildGameURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0);
        List<Game> gameList = getGameList(searchTwitch(url));
        if (gameList.size() != 0) {
            return gameList.get(0);
        }
        return null;
    }

    // Convert the json data returned from calling twitch API to an Arraylist of Item objects
    private List<Item> getItemList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Item[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse item data from Twitch API");
        }
    }

    // Return the top streams based on gameId
    private List<Item> searchStreams(String gameId, int limit) throws TwitchException {
        String url = buildSearchURL(STREAM_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> streams = getItemList(searchTwitch(url));
        for (Item item : streams) {
            item.setType(ItemType.STREAM);
            //The returned data doesn't contain an url
            item.setUrl(TWITCH_BASE_URL + item.getBroadcasterName());
        }
        return streams;
    }

    // Return the top clips based on gameId
    private List<Item> searchClips(String gameId, int limit) throws TwitchException {
        String url = buildSearchURL(CLIP_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> clips = getItemList(searchTwitch(url));
        for (Item item : clips) {
            item.setType(ItemType.CLIP);
        }
        return clips;
    }

    // Return the top videos based on gameId
    private List<Item> searchVideos(String gameId, int limit) throws TwitchException {
        String url = buildSearchURL(VIDEO_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> videos = getItemList(searchTwitch(url));
        for (Item item : videos) {
            item.setType(ItemType.VIDEO);
        }
        return videos;
    }

    //Return a list of Item objects for a game based on different types
    public List<Item> searchByType(String gameId, ItemType type, int limit) throws TwitchException {
        List<Item> items = new ArrayList<>();

        switch (type) {
            case STREAM:
                items = searchStreams(gameId, limit);
                break;
            case VIDEO:
                items = searchVideos(gameId, limit);
                break;
            case CLIP:
                items = searchClips(gameId, limit);
                break;
        }

        //Some returned data doesn't contain a gameId
        for (Item item : items) {
            item.setGameId(gameId);
        }
        return items;
    }

    //Search all types(stream, video, clip) and return a list of Item objects for a game
    public Map<String, List<Item>> searchItems(String gameId) throws TwitchException {
        Map<String, List<Item>> itemMap = new HashMap<>();
        for (ItemType type : ItemType.values()) {
            itemMap.put(type.toString(), searchByType(gameId, type, DEFAULT_SEARCH_LIMIT));
        }
        return itemMap;
    }
}
