package com.redgear.cog;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Driver;

public class CogDataSourceBuilder {

    private final CogClientBuilder builder;
    private final BasicDataSource source = new BasicDataSource();


    public CogDataSourceBuilder(CogClientBuilder builder) {
        this.builder = builder;
    }

    public CogDataSourceBuilder setUrl(String url) {
        source.setUrl(url);
        return this;
    }

    public CogDataSourceBuilder setDriverClassName(String driverClassName) {
        source.setDriverClassName(driverClassName);
        return this;
    }

    public CogDataSourceBuilder setDriver(Driver driver) {
        source.setDriver(driver);
        return this;
    }

    public CogDataSourceBuilder setUsername(String username) {
        source.setUsername(username);
        return this;
    }

    public CogDataSourceBuilder setPassword(String password) {
        source.setPassword(password);
        return this;
    }


    public CogClientBuilder build() {
        return builder.setDataSource(source);
    }

}
