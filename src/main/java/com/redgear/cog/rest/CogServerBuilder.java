package com.redgear.cog.rest;

public interface CogServerBuilder {
    CogServerBuilder useJetty();

    CogServerBuilder setServerType(CogServerType serverType);

    CogServerBuilder setPort(int port);

    CogServer build();
}
