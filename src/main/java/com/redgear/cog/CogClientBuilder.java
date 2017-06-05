package com.redgear.cog;

import com.redgear.cog.exception.CogConfigurationException;
import com.redgear.cog.impl.Config;
import com.redgear.cog.impl.TypeConverter;
import com.redgear.cog.impl.converters.IntegerConverter;
import com.redgear.cog.impl.converters.LongConverter;
import com.redgear.cog.impl.converters.StringConverter;
import com.redgear.cog.impl.resultmapper.*;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CogClientBuilder {

    private static final ArrayList<TypeConverter> baseConverters = new ArrayList<>();
    private static final ArrayList<CogResultMapperFactory> baseMapperFactorys = new ArrayList<>();
    private final List<TypeConverter> converters = new ArrayList<>(baseConverters);
    private final List<CogResultMapperFactory> resultMapperFactoryList = new ArrayList<>(baseMapperFactorys);
    private ExecutorService ioPool;
    private DataSource source;


    static {
        baseConverters.add(new StringConverter());
        baseConverters.add(new LongConverter());
        baseConverters.add(new IntegerConverter());
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

    public CogClientBuilder setDataSource(DataSource source) {
        this.source = source;
        return this;
    }

    public CogDataSourceBuilder buildDataSource() {
        return new CogDataSourceBuilder(this);
    }

    public CogClientBuilder addTypeConverter(TypeConverter converter) {
        converters.add(converter);
        return this;
    }

    public CogClientBuilder addTypeConverters(TypeConverter ...converters) {
        Collections.addAll(this.converters, converters);
        return this;
    }

    public CogClientBuilder addTypeConverters(Collection<TypeConverter> converters) {
        this.converters.addAll(converters);
        return this;
    }

    public CogClientBuilder setExecutorService(ExecutorService ioPool) {
        this.ioPool = ioPool;
        return this;
    }

    public CogClientBuilder setExecutorServiceThreads(int threadCount) {
        ioPool = Executors.newFixedThreadPool(threadCount);
        return this;
    }

    public CogClientBuilder addResultMapperFactory(CogResultMapperFactory factory) {
        this.resultMapperFactoryList.add(factory);
        return this;
    }

    public CogClient build() {
        if(source == null) {
            throw new CogConfigurationException("No data source provided!");
        }

        if(ioPool == null) {
            ioPool = Executors.newFixedThreadPool(10);
        }

        return new CogClient(new Config(source, converters, resultMapperFactoryList, ioPool));
    }

}
