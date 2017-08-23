package com.redgear.cog.repo;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * Created by LordBlackHole on 2017-06-05.
 */
public interface CogClientBuilder {
    CogClientBuilder setDataSource(DataSource source);

    CogDataSourceBuilder buildDataSource();

    CogClientBuilder addTypeConverter(TypeConverter converter);

    CogClientBuilder addTypeConverters(TypeConverter... converters);

    CogClientBuilder addTypeConverters(Collection<TypeConverter> converters);

    CogClientBuilder setExecutorService(ExecutorService ioPool);

    CogClientBuilder setExecutorServiceThreads(int threadCount);

    CogClientBuilder addResultMapperFactory(CogResultMapperFactory factory);

    CogClient build();
}
