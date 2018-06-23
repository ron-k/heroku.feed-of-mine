package com.rndysoft.linkofmy.model;

import java.util.List;

public class FeedModel {
    private final List<FeedItem> items;

    public FeedModel(List<FeedItem> items) {
        this.items = items;
    }

    public List<FeedItem> getItems() {
        return items;
    }
}
