package com.rndysoft.linkofmy.handlers;

import com.google.common.base.Strings;
import com.rndysoft.linkofmy.db.Schema;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.internal.DefaultStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static ratpack.groovy.Groovy.groovyTemplate;

public class HandlerAddLink implements Handler {

    private static final String PARAM_LINK = "link";
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HandlerAddLink.class);

    @Override
    public void handle(Context ctx) throws Exception {
        String link = ctx.getRequest().getQueryParams().get(PARAM_LINK);
        LOGGER.error("handle: link={}", link);
        if (Strings.isNullOrEmpty(link)) {
            throw new MissingParameterException();
        }
        insertLink(ctx, link)
                .then(attributes -> ctx.getResponse().status(new DefaultStatus(HttpResponseStatus.CREATED)).send());
    }

    private Promise<Object> insertLink(Context ctx, String link) {
        return Blocking.get(() -> {
            try (Connection connection = ctx.get(DataSource.class).getConnection()) {
                ensureTableExists(connection);
                PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %s (%s) VALUES (?)", Schema.TABLE_LINKS, Schema.COLUMN_LINK));
                statement.setString(1, link);
                statement.execute();
                return null;
            }
        }).onError(throwable -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "There was an error: " + throwable);
            ctx.render(groovyTemplate(attributes, "error.html"));
        });
    }

    private void ensureTableExists(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + Schema.TABLE_LINKS + " (" +
                Schema.COLUMN_LINK + " text PRIMARY KEY," +
                Schema.COLUMN_TIME_ADDED + " timestamp DEFAULT current_timestamp" +
                ")");
    }

    private class MissingParameterException extends Exception {
    }
}
