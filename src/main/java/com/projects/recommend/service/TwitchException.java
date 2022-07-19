package com.projects.recommend.service;

public class TwitchException extends RuntimeException {
    public TwitchException(String errorMessage) {
        super(errorMessage);
    }
}