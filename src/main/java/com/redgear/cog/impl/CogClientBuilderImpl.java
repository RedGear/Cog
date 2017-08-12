package com.redgear.cog.impl;

import com.redgear.cog.CogClient;
import com.redgear.cog.CogClientBuilder;
import com.redgear.cog.CogDataSourceBuilder;
import com.redgear.cog.CogResultMapperFactory;
import com.redgear.cog.exception.CogConfigurationException;
import com.redgear.cog.impl.resultmapper.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CogClientBuilderImpl implements CogClientBuilder {

    private static final ArrayList<TypeConverter> baseConverters = new ArrayList<>();
    private static final ArrayList<CogResultMapperFactory> baseMapperFactorys = new ArrayList<>();
    private final List<TypeConverter> converters = new ArrayList<>(baseConverters);
    private final List<CogResultMapperFactory> resultMapperFactoryList = new ArrayList<>(baseMapperFactorys);
    private ExecutorService ioPool;
    private DataSource source;


    static {
        addBaseConverter(String.class, ResultContext::getString);
        addBaseConverter(Boolean.class, ResultContext::getBoolean);
        addBaseConverter(Byte.class, ResultContext::getByte);
        addBaseConverter(Short.class, ResultContext::getShort);
        addBaseConverter(Integer.class, ResultContext::getInt);
        addBaseConverter(Float.class, ResultContext::getFloat);
        addBaseConverter(Double.class, ResultContext::getDouble);
        addBaseConverter(boolean.class, ResultContext::getBoolean);
        addBaseConverter(byte.class, ResultContext::getByte);
        addBaseConverter(short.class, ResultContext::getShort);
        addBaseConverter(int.class, ResultContext::getInt);
        addBaseConverter(float.class, ResultContext::getFloat);
        addBaseConverter(double.class, ResultContext::getDouble);
        addBaseConverter(byte[].class, ResultContext::getBytes);
        addBaseConverter(Array.class, ResultContext::getArray);
        addBaseConverter(InputStream.class, ResultContext::getAsciiStream);
        addBaseConverter(BigDecimal.class, ResultContext::getBigDecimal);
        addBaseConverter(InputStream.class, ResultContext::getBinaryStream);
        addBaseConverter(Blob.class, ResultContext::getBlob);
        addBaseConverter(Reader.class, ResultContext::getCharacterStream);
        addBaseConverter(Clob.class, ResultContext::getClob);
        addBaseConverter(Date.class, ResultContext::getDate);
        addBaseConverter(Ref.class, ResultContext::getRef);
        addBaseConverter(SQLXML.class, ResultContext::getSQLXML);
        addBaseConverter(Time.class, ResultContext::getTime);
        addBaseConverter(Timestamp.class, ResultContext::getTimestamp);
        addBaseConverter(URL.class, ResultContext::getURL);
        addBaseConverter(NClob.class, ResultContext::getNClob);
        baseConverters.trimToSize();

        baseMapperFactorys.add(CollectionResultMapperFactory.build(ArrayList.class, List.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(ArrayList.class, ArrayList.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(ArrayList.class, Collection.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(LinkedList.class, LinkedList.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(HashSet.class, Set.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(HashSet.class, HashSet.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(LinkedHashSet.class, LinkedHashSet.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(TreeSet.class, TreeSet.class));
        baseMapperFactorys.add(CollectionResultMapperFactory.build(TreeSet.class, SortedSet.class));
        baseMapperFactorys.add(new CompletableFutureResultMapperFactory());
        baseMapperFactorys.add(new FutureResultMapperFactory());
        baseMapperFactorys.add(new IterableResultMapperFactory());
        baseMapperFactorys.add(new IteratorResultMapperFactory());
        baseMapperFactorys.add(new OptionalResultMapperFactory());
        baseMapperFactorys.add(new StreamResultMapperFactory());
        baseMapperFactorys.trimToSize();
    }

    private interface SQLFunction<T> {
        T apply(ResultContext context) throws SQLException;
    }

    private static <T> void addBaseConverter(Class<T> clazz, SQLFunction<T> getter) {
        baseConverters.add(new TypeConverter<T>() {
            @Override
            public Class<T> getType() {
                return clazz;
            }

            @Override
            public T read(ResultContext context) throws SQLException {
                return getter.apply(context);
            }
        });
    }

    @Override public CogClientBuilder setDataSource(DataSource source) {
        this.source = source;
        return this;
    }

    @Override public CogDataSourceBuilder buildDataSource() {
        return new CogDataSourceBuilderImpl(this);
    }

    @Override public CogClientBuilder addTypeConverter(TypeConverter converter) {
        converters.add(converter);
        return this;
    }

    @Override public CogClientBuilder addTypeConverters(TypeConverter ...converters) {
        Collections.addAll(this.converters, converters);
        return this;
    }

    @Override public CogClientBuilder addTypeConverters(Collection<TypeConverter> converters) {
        this.converters.addAll(converters);
        return this;
    }

    @Override public CogClientBuilder setExecutorService(ExecutorService ioPool) {
        this.ioPool = ioPool;
        return this;
    }

    @Override public CogClientBuilder setExecutorServiceThreads(int threadCount) {
        ioPool = Executors.newFixedThreadPool(threadCount);
        return this;
    }

    @Override public CogClientBuilder addResultMapperFactory(CogResultMapperFactory factory) {
        this.resultMapperFactoryList.add(factory);
        return this;
    }

    @Override public CogClient build() {
        if(source == null) {
            throw new CogConfigurationException("No data source provided!");
        }

        if(ioPool == null) {
            ioPool = Executors.newFixedThreadPool(10);
        }

        return new CogClientImpl(new Config(source, converters, resultMapperFactoryList, ioPool));
    }

}
