package com.redgear.cog.impl;

public interface ResultSourceHandler<In> {
    void next(In in);

    void error(Throwable t);

    void complete();
}
