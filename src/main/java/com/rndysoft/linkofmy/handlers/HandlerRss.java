package com.rndysoft.linkofmy.handlers;

import com.rndysoft.linkofmy.db.Schema;
import com.rndysoft.linkofmy.model.FeedItem;
import com.rndysoft.linkofmy.model.FeedModel;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static ratpack.groovy.Groovy.groovyTemplate;

public class HandlerRss implements Handler {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HandlerRss.class);

    @Override
    public void handle(Context ctx) throws Exception {
        Blocking.get(() -> {
            List<FeedItem> items = getFeedItems(ctx);
            Map<String, FeedModel> out = new HashMap<>();
            out.put("feed", new FeedModel(items));
            LOGGER.error("handle: attributes=%s", out);
            return out;

        }).onError(throwable -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "There was an error: " + throwable);
            ctx.render(groovyTemplate(attributes, "error.html"));
        }).then(attributes -> {
            ctx.render(groovyTemplate(attributes, "feed.xml"));
        });
    }

    private List<FeedItem> getFeedItems(Context ctx) throws SQLException {
        List<FeedItem> items = new ArrayList<>();
        try (Connection connection = ctx.get(DataSource.class).getConnection()) {
            Statement stmt = connection.createStatement();
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + Schema.TABLE_LINKS + " ORDER BY " + Schema.COLUMN_TIME_ADDED)) {
                while (rs.next()) {
                    items.add(new FeedItem(rs.getString(Schema.COLUMN_LINK), rs.getTimestamp(Schema.COLUMN_TIME_ADDED)));
                }
            }
        }
        return items;
    }

    private List<FeedItem> getFeedItems2(Context ctx) throws SQLException {
        List<FeedItem> items = new ArrayList<>();
        items.add(new FeedItem("magnet:?xt=urn:btih:6066125b85f3449bb2fffa91041acde48d8b1992&dn=rutor.info_%D0%A7%D1%91%D1%80%D0%BD%D0%B0%D1%8F+%D0%B2%D0%B4%D0%BE%D0%B2%D0%B0+%2F+Black+widow+%282008%29+WEB-DL+1080p+%D0%BE%D1%82+KORSAR+%7C+P2&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce", new Date()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        items.add(new FeedItem("http://d.rutor.info/download/638061", new Date()));
        return items;
    }

}
