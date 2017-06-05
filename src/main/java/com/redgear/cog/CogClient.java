package com.redgear.cog;

import com.redgear.cog.impl.Config;
import com.redgear.cog.impl.RepositoryFactory;
import com.redgear.cog.impl.TypeData;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CogClient {

    private static final Pattern pattern = Pattern.compile("(:\\w+)");
    private final Config config;
    private final RepositoryFactory repositoryFactory;

    public CogClient(Config config) {
        this.config = config;
        this.repositoryFactory = new RepositoryFactory(this, config);
    }

    public static CogClientBuilder builder() {
        return new CogClientBuilder();
    }

    public <T> T buildRepository(Class<T> clazz) {
        return repositoryFactory.create(clazz);
    }

    public <T> CogStatement<T> prepareStatement(@Language("SQL") String query, Class<T> resultType) {
        // Process named params
        List<String> params = new ArrayList<>();
        Matcher matcher = pattern.matcher(query);

        while(matcher.find()) {
            // Substring 1 to cut out the ':'
            params.add(matcher.group(1).substring(1));
        }

        String preparedQuery = matcher.replaceAll("?");

        TypeData<T> typeData = config.getTypeData(resultType);
        return new CogStatement<>(config, preparedQuery, params, typeData);
    }

}
