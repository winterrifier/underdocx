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

package de.underdocx.environment;

import java.util.Optional;
import java.util.function.Supplier;

public class UnderdocxExecutionException extends RuntimeException {
    public UnderdocxExecutionException() {
    }

    public UnderdocxExecutionException(String message) {
        super(message);
    }

    public UnderdocxExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnderdocxExecutionException(Throwable cause) {
        super(cause);
    }

    public UnderdocxExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public static <T> T expect(T value, String valueDescription) {
        if (value == null) {
            throw new UnderdocxExecutionException("expected value is missing (null): " + valueDescription);
        }
        return value;
    }

    public static <T> T expect(T value) {
        if (value == null) {
            throw new UnderdocxExecutionException("expected value is missing (null)");
        }
        return value;
    }

    public static <T> T expect(Optional<T> value, String valueDescription) {
        if (value == null && value.isEmpty()) {
            throw new UnderdocxExecutionException("expected value is missing (Optional.empty): " + valueDescription);
        }
        return value.get();
    }

    public static <T> T expect(Optional<T> value) {
        if (value == null && value.isEmpty()) {
            throw new UnderdocxExecutionException("expected value is missing (Optional.empty)");
        }
        return value.get();
    }

    public static void failIf(boolean condition, String message) {
        if (condition) throw new UnderdocxExecutionException(message);
    }

    public static void failIf(boolean condition) {
        if (condition) throw new UnderdocxExecutionException();
    }

    public static <T> T exec(Supplier<T> r) {
        try {
            return r.get();
        } catch (Exception e) {
            throw new UnderdocxExecutionException(e);
        }
    }


}
