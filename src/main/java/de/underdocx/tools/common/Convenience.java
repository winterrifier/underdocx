/*
MIT License

Copyright (c) 2024 Gerald Winter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package de.underdocx.tools.common;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Convenience {


    public static <Y, X extends Collection<Y>> X add(X collection, Y element) {
        collection.add(element);
        return collection;
    }

    public static <Y, X extends Collection<Y>> Y push(X collection, Y element) {
        collection.add(element);
        return element;
    }

    public static <T> T also(T returnValue, Consumer<T> consumer) {
        consumer.accept(returnValue);
        return returnValue;
    }


    public static <E> List<E> buildList(Consumer<List<E>> consumer) {
        return also(new ArrayList<>(), consumer::accept);
    }

    public static String buildString(Consumer<StringBuilder> consumer) {
        return also(new StringBuilder(), consumer).toString();
    }

    public static <T> Optional<T> buildOptional(Consumer<Wrapper<T>> consumer) {
        return also(new Wrapper<>(), consumer).toOptional();
    }

    public static <T> Optional<T> buildOptional(T initValue, Consumer<Wrapper<T>> consumer) {
        return also(new Wrapper<>(initValue), consumer).toOptional();
    }

    public static <T> T build(Consumer<Wrapper<T>> consumer) {
        return also(new Wrapper<>(), consumer).value;
    }

    public static <T> T build(T initValue, Consumer<Wrapper<T>> consumer) {
        return also(new Wrapper<>(initValue), consumer).value;
    }


    public static <E> List<E> reverse(List<E> list) {
        Collections.reverse(new ArrayList<>(list));
        return list;
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> Optional<R> findFirstPresent(Collection<T> collection, Function<T, Optional<R>> f) {
        for (T element : collection) {
            Optional<R> result = f.apply(element);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    public static <T> Optional<T> findFirst(Collection<T> collection, Predicate<T> filter) {
        return collection.stream().filter(filter).findFirst();
    }

    public static <R> R getOrDefault(R value, R defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    public static <I, R> R getOrDefault(Optional<I> value, Function<I, R> provider, R defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : provider.apply(value.get());
    }

    public static <T> boolean all(Collection<T> collection, Predicate<T> predicate) {
        if (collection.isEmpty()) return true;
        for (T element : collection) {
            if (!predicate.test(element)) {
                return false;
            }
        }
        return true;
    }

    public static <T> T first(Iterable<T> collection) {
        return collection.iterator().next();
    }


}
