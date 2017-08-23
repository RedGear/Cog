package com.redgear.cog.core;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<Key, Value> {

    private final Map<Key, Value> map = new HashMap<>();

    public static <Key, Value> MapBuilder<Key, Value> mapOf(Key key, Value value) {
        return new MapBuilder<Key, Value>().put(key, value);
    }

    public MapBuilder<Key, Value> put(Key key, Value value) {
        map.put(key, value);
        return this;
    }

    public Map<Key, Value> build() {
        return map;
    }
}
