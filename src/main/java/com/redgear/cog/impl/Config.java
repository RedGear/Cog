package com.redgear.cog.impl;

import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.TypeConverter;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Config {

    private final DataSource source;

    private final QueryRunner runner;
    private final Map<Class<?>, TypeConverter> converterMap = new HashMap<>();
    private final Map<Class<?>, TypeData<?>> typeDataCache = new HashMap<>();
    private final TypeDataFactory typeDataFactory = new TypeDataFactory(this);
    private final Map<Class<?>, CogResultMapperFactory> resultMapperFactoryMap = new HashMap<>();
    private final ExecutorService ioPool;

    public Config(DataSource source, List<TypeConverter> converterMap, List<CogResultMapperFactory> resultMapperFactoryList, ExecutorService ioPool) {
        this.source = source;
        this.ioPool = ioPool;
        converterMap.forEach(typeConverter -> this.converterMap.put(typeConverter.getType(), typeConverter));
        resultMapperFactoryList.forEach(factory -> this.resultMapperFactoryMap.put(factory.type(), factory));
        runner = new QueryRunner(source);
    }

    public DataSource getSource() {
        return source;
    }


    public <T> TypeConverter<T> getConverter(Class<T> clazz) {
        return converterMap.get(clazz);
    }

    public <T> TypeData<T> getTypeData(Class<T> clazz) {
        TypeData<T> data = (TypeData<T>) typeDataCache.get(clazz);

        if (data == null) {
            TypeConverter<T> converter = converterMap.get(clazz);

            if (converter != null) {
                data = new SimpleTypeData<>(converter);
            } else {
                data = typeDataFactory.build(clazz);
            }

            typeDataCache.put(clazz, data);
        }

        return data;
    }

    public CogResultMapperFactory getResultMapperFactory(Class clazz) {
        return resultMapperFactoryMap.get(clazz);
    }

    public QueryRunner getRunner() {
        return runner;
    }

    public ExecutorService getIoPool() {
        return ioPool;
    }


}
