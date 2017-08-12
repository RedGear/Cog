package com.redgear.cog.impl;

import com.redgear.cog.CogClient;
import com.redgear.cog.CogStatement;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CogClientImpl implements CogClient {

    private static final Pattern pattern = Pattern.compile("(:\\w+)");
    private final Config config;
    private final RepositoryFactory repositoryFactory;

    public CogClientImpl(Config config) {
        this.config = config;
        this.repositoryFactory = new RepositoryFactory(this, config);
    }

    @Override public <T> T buildRepository(Class<T> clazz) {
        return repositoryFactory.create(clazz);
    }

    @Override public <T> CogStatement<T> prepareStatement(@Language("SQL") String query, Class<T> resultType) {
        // Process named params
        List<String> params = new ArrayList<>();
        Matcher matcher = pattern.matcher(query);

        while(matcher.find()) {
            // Substring 1 to cut out the ':'
            params.add(matcher.group(1).substring(1));
        }

        String preparedQuery = matcher.replaceAll("?");

        TypeData<T> typeData = config.getTypeData(resultType);
        return new CogStatementImpl<>(config, preparedQuery, params, typeData);
    }

}
