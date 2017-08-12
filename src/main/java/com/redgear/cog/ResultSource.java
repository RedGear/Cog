package com.redgear.cog;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

public interface ResultSource<In> {

    <Out> ResultSource<Out> map(Function<In, Out> func);

    <Out> ResultSource<Out> flatMap(Function<In, ResultSource<Out>> func);

    ResultSource<In> filter(Predicate<In> func);


    CompletableFuture<Void> forEach(Consumer<In> func);

    CompletableFuture<Void> forEach(Consumer<In> func, Consumer<Throwable> errorHandler);

    CompletableFuture<List<In>> toList();

    CompletableFuture<In> reduce(BiFunction<In, In, In> func);

    <Out> CompletableFuture<Out> fold(BiFunction<Out, In, Out> func, Out start);


    CompletableFuture<List<Throwable>> errors();

    <Out> ResultSource<Out> recover(BiFunction<In, Throwable, Out> func);

    ResultSource<In> handle(Consumer<Throwable> func);

    ResultSource<In> ignoreErrors();
}
