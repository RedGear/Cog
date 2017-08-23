package com.redgear.cog.rest;

public interface ParamMapper<T> {

    Class<T> getType();

    T parse(String input) throws Exception;

}
