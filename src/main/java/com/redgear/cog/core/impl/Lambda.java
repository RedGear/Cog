package com.redgear.cog.core.impl;

import org.apache.commons.collections.iterators.EmptyIterator;

import java.util.*;
import java.util.function.BiFunction;

public class Lambda {

    public static <In, Out> Out foldLeft(Out result, Iterable<In> source, BiFunction<Out, In, Out> func) {

        for(In next : source) {
            result = func.apply(result, next);
        }

        return result;
    }

    public static <Left, Right, Out> List<Out> zip(Iterable<Left> leftSource, Iterable<Right> rightSource, BiFunction<Left, Right, Out> func) {

        Iterator<Left> left = leftSource.iterator();
        Iterator<Right> right = rightSource.iterator();

        List<Out> out = new ArrayList<>();

        while(left.hasNext() && right.hasNext()) {
            out.add(func.apply(left.next(), right.next()));
        }

        return out;
    }

    public static <Left, Right> Map<Left, Right> zipMap(Iterable<Left> leftSource, Iterable<Right> rightSource) {

        Iterator<Left> left = leftSource.iterator();
        Iterator<Right> right = rightSource.iterator();

        Map<Left, Right> out = new HashMap<>();

        while(left.hasNext() && right.hasNext()) {
            out.put(left.next(), right.next());
        }

        return out;
    }

    public static <Left, Right> Map<Left, Right> zipMap(Iterator<Left> left, Iterator<Right> right) {

        Map<Left, Right> out = new HashMap<>();

        while(left.hasNext() && right.hasNext()) {
            out.put(left.next(), right.next());
        }

        return out;
    }

    public static <Out> Iterator<Out> arrayIterator(Out[] array) {
        if(array == null) {
            return (Iterator<Out>) EmptyIterator.INSTANCE;
        }

        return new Iterator<Out>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return array.length > index;
            }

            @Override
            public Out next() {
                return array[index++];
            }
        };
    }
}
