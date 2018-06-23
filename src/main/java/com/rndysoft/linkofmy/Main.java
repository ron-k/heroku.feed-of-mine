package com.rndysoft.linkofmy;

import com.rndysoft.linkofmy.handlers.HandlerAddLink;
import com.rndysoft.linkofmy.handlers.HandlerRss;
import ratpack.groovy.template.TextTemplateModule;
import ratpack.guice.Guice;
import ratpack.hikari.HikariModule;
import ratpack.http.Headers;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import static ratpack.groovy.Groovy.groovyTemplate;

public class Main {
    public static void main(String... args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c
                        .baseDir(BaseDir.find())
                        .env())

                .registry(Guice.registry(b -> {
                    if (System.getenv("JDBC_DATABASE_URL") != null) {
                        b.module(HikariModule.class, conf -> {
                            conf.addDataSourceProperty("URL", System.getenv("JDBC_DATABASE_URL"));
                            conf.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
                        });
                    }
                    b.module(TextTemplateModule.class, conf -> conf.setStaticallyCompile(false));
                }))

                .handlers(chain -> chain
                        .get(ctx -> ctx.render(groovyTemplate("index.html")))

                        .get("hello", ctx -> {
                            Headers headers = ctx.getRequest().getHeaders();
                            ctx.render(headers.asMultiValueMap().toString());
                        })

                        .get("add", new HandlerAddLink())
                        .get("feed", new HandlerRss())
                        .files(f -> f.dir("public"))
                )
        );
    }

}