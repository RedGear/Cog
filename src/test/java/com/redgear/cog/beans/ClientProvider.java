package com.redgear.cog.beans;

import com.redgear.cog.repo.CogClient;
import com.redgear.cog.rest.CogServer;

public class ClientProvider {

    private static final CogClient client;
    private static final CogServer server;

    static {
        client = CogClient.builder()
                .buildDataSource()
                    .setDriverClassName("org.postgresql.Driver")
                    .setUrl("jdbc:postgresql://localhost:5432/postgres")
                    .setUsername("postgres")
                    .setPassword("mysecretpassword")

//                .setDriverClassName("com.mysql.cj.jdbc.Driver")
//                .setUrl("jdbc:mysql://localhost:3306/test")
//                .setUsername("root")
//                .setPassword("my-secret-pw")

                .build()
                .build();

        server = CogServer.builder().build();
    }


    public static CogClient getClient() {
        return client;
    }

    public static CogServer getServer() {
        return server;
    }
}
