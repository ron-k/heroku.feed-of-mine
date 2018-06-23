package com.rndysoft.linkofmy.model;

import java.util.Date;
import java.util.Objects;

public class FeedItem {
    private final String link;
    private final Date timeAdded;

    public FeedItem(String link, Date timeAdded) {
        this.link = link;
        this.timeAdded = timeAdded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }

    public Date getTimeAdded() {
        return timeAdded;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "link='" + link + '\'' +
                ", timeAdded=" + timeAdded +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedItem feedItem = (FeedItem) o;
        return Objects.equals(link, feedItem.link);
    }
}
