package com.redgear.cog.impl;

import com.redgear.cog.CogClientBuilder;
import com.redgear.cog.CogDataSourceBuilder;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Driver;

public class CogDataSourceBuilderImpl implements CogDataSourceBuilder {

    private final CogClientBuilder builder;
    private final BasicDataSource source = new BasicDataSource();


    public CogDataSourceBuilderImpl(CogClientBuilder builder) {
        this.builder = builder;
    }

    @Override public CogDataSourceBuilder setUrl(String url) {
        source.setUrl(url);
        return this;
    }

    @Override public CogDataSourceBuilder setDriverClassName(String driverClassName) {
        source.setDriverClassName(driverClassName);
        return this;
    }

    @Override public CogDataSourceBuilder setDriver(Driver driver) {
        source.setDriver(driver);
        return this;
    }

    @Override public CogDataSourceBuilder setUsername(String username) {
        source.setUsername(username);
        return this;
    }

    @Override public CogDataSourceBuilder setPassword(String password) {
        source.setPassword(password);
        return this;
    }


    @Override public CogClientBuilder build() {
        return builder.setDataSource(source);
    }

}
