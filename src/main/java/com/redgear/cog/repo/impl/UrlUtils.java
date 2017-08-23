package com.redgear.cog.repo.impl;

import com.redgear.cog.exception.CogRestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtils {

    public static Map<String, String> parseQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(pair -> decode(pair[0]), pair -> decode(pair[1])));
    }

    private static String decode(String str){
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CogRestClientException("Cannot URL decode query key/value: '" + str + "'");
        }
    }

}
