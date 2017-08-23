package com.redgear.cog.repo;

import java.sql.Driver;

/**
 * Created by LordBlackHole on 2017-06-05.
 */
public interface CogDataSourceBuilder {
    CogDataSourceBuilder setUrl(String url);

    CogDataSourceBuilder setDriverClassName(String driverClassName);

    CogDataSourceBuilder setDriver(Driver driver);

    CogDataSourceBuilder setUsername(String username);

    CogDataSourceBuilder setPassword(String password);

    CogClientBuilder build();
}
