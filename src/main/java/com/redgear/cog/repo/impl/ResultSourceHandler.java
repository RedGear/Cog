package com.redgear.cog.repo.impl;

public interface ResultSourceHandler<In> {
    void next(In in);

    void error(Throwable t);

    void complete();
}
