package com.redgear.cog.impl;

import com.redgear.cog.ResultSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ResultSourceImpl<In> implements ResultSource<In>, ResultSourceHandler<In> {

    private List<ResultSourceHandler<In>> handlers = new ArrayList<>();

    public void next(In in) {
        handlers.forEach(handler -> handler.next(in));
    }

    public void error(Throwable t) {
        handlers.forEach(handler -> handler.error(t));
    }

    public void complete() {
        handlers.forEach(ResultSourceHandler::complete);
        handlers = null; // Mark for GC
    }


    @Override
    public <Out> ResultSource<Out> map(Function<In, Out> func) {
        ResultSourceImpl<Out> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    out.next(func.apply(in));
                } catch (Exception e) {
                    out.error(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.error(t);
            }

            @Override
            public void complete() {
                out.complete();
            }
        });

        return out;
    }

    @Override
    public <Out> ResultSource<Out> flatMap(Function<In, ResultSource<Out>> func) {
        ResultSourceImpl<Out> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {

            List<CompletableFuture<Void>> finished = new ArrayList<>();

            @Override
            public void next(In in) {
                try {
                    finished.add(func.apply(in).forEach(out::next, out::error));
                } catch (Exception e) {
                    out.error(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.error(t);
            }

            @Override
            public void complete() {
                CompletableFuture.allOf(finished.toArray(new CompletableFuture[]{})).thenRun(out::complete);
                finished = null;
            }
        });

        return out;
    }

    @Override
    public ResultSource<In> filter(Predicate<In> func) {
        ResultSourceImpl<In> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    if (func.test(in)) {
                        out.next(in);
                    }
                } catch (Exception e) {
                    out.error(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.error(t);
            }

            @Override
            public void complete() {
                out.complete();
            }
        });

        return out;
    }

    @Override
    public CompletableFuture<Void> forEach(Consumer<In> func) {
        CompletableFuture<Void> out = new CompletableFuture<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    func.accept(in);
                } catch (Exception e) {
                    out.completeExceptionally(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.completeExceptionally(t);
            }

            @Override
            public void complete() {
                out.complete(null);
            }
        });

        return out;
    }

    @Override
    public CompletableFuture<Void> forEach(Consumer<In> func, Consumer<Throwable> errorHandler) {
        CompletableFuture<Void> out = new CompletableFuture<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    func.accept(in);
                } catch (Exception e) {
                    out.completeExceptionally(e);
                }
            }

            @Override
            public void error(Throwable t) {
                errorHandler.accept(t);
            }

            @Override
            public void complete() {
                out.complete(null);
            }
        });

        return out;
    }

    @Override
    public CompletableFuture<List<In>> toList() {
        CompletableFuture<List<In>> out = new CompletableFuture<>();
        ArrayList<In> result = new ArrayList<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    result.add(in);
                } catch (Exception e) {
                    out.completeExceptionally(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.completeExceptionally(t);
            }

            @Override
            public void complete() {
                result.trimToSize();
                out.complete(result);
            }
        });

        return out;
    }

    @Override
    public CompletableFuture<In> reduce(BiFunction<In, In, In> func) {
        CompletableFuture<In> out = new CompletableFuture<>();

        handlers.add(new ResultSourceHandler<In>() {

            In result = null;

            @Override
            public void next(In in) {
                try {
                    if (result == null) {
                        result = in;
                    } else {
                        result = func.apply(result, in);
                    }
                } catch (Exception e) {
                    out.completeExceptionally(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.completeExceptionally(t);
            }

            @Override
            public void complete() {
                out.complete(result);
            }
        });

        return out;
    }

    @Override
    public <Out> CompletableFuture<Out> fold(BiFunction<Out, In, Out> func, Out start) {
        CompletableFuture<Out> out = new CompletableFuture<>();

        handlers.add(new ResultSourceHandler<In>() {
            Out result = start;

            @Override
            public void next(In in) {
                try {
                    result = func.apply(result, in);
                } catch (Exception e) {
                    out.completeExceptionally(e);
                }
            }

            @Override
            public void error(Throwable t) {
                out.completeExceptionally(t);
            }

            @Override
            public void complete() {
                out.complete(result);
            }
        });

        return out;
    }

    @Override
    public CompletableFuture<List<Throwable>> errors() {
        CompletableFuture<List<Throwable>> out = new CompletableFuture<>();
        ArrayList<Throwable> result = new ArrayList<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {

            }

            @Override
            public void error(Throwable t) {
                result.add(t);
            }

            @Override
            public void complete() {
                result.trimToSize();
                out.complete(result);
            }
        });

        return out;
    }

    @Override
    public <Out> ResultSource<Out> recover(BiFunction<In, Throwable, Out> func) {
        ResultSourceImpl<Out> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                try {
                    out.next(func.apply(in, null));
                } catch (Exception e) {
                    out.error(e);
                }
            }

            @Override
            public void error(Throwable t) {
                try {
                    out.next(func.apply(null, t));
                } catch (Exception e) {
                    out.error(e);
                }
            }

            @Override
            public void complete() {
                out.complete();
            }
        });

        return out;
    }

    @Override
    public ResultSource<In> handle(Consumer<Throwable> func) {
        ResultSourceImpl<In> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                out.next(in);
            }

            @Override
            public void error(Throwable t) {
                func.accept(t);
            }

            @Override
            public void complete() {
                out.complete();
            }
        });

        return out;
    }

    @Override
    public ResultSource<In> ignoreErrors() {
        ResultSourceImpl<In> out = new ResultSourceImpl<>();

        handlers.add(new ResultSourceHandler<In>() {
            @Override
            public void next(In in) {
                out.next(in);
            }

            @Override
            public void error(Throwable t) {

            }

            @Override
            public void complete() {
                out.complete();
            }
        });

        return out;
    }
}
