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

package de.underdocx.environment.logger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logger interface that can be implemented to use any other logger framework.
 */
public interface Logger {

    static String formatMessage(LogLevel level, String message, Throwable e) {
        String stackTrace = null;
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            stackTrace = sw.toString();
        }
        if (e == null) {
            return level + ", " + message;
        }
        if (message == null) {
            return level + "\n" + stackTrace;
        }
        return level + ", " + message + "\n" + stackTrace;
    }

    default void log(LogLevel level, String message, Throwable e) {
        PrintStream out = level.compareTo(LogLevel.ERROR) >= 0 ? System.err : System.out;
        out.println(formatMessage(level, message, e));
    }

    default void trace(String message) {
        log(LogLevel.TRACE, message, null);
    }

    default void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    default void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    default void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    default void fatal(String message) {
        log(LogLevel.FATAL, message, null);
    }

    default void warn(String message, Throwable e) {
        log(LogLevel.WARN, message, e);
    }

    default void error(String message, Throwable e) {
        log(LogLevel.ERROR, message, e);
    }

    default void fatal(String message, Throwable e) {
        log(LogLevel.FATAL, message, e);
    }

    default void warn(Throwable e) {
        log(LogLevel.WARN, null, e);
    }

    default void error(Throwable e) {
        log(LogLevel.ERROR, null, e);
    }

    default void fatal(Throwable e) {
        log(LogLevel.FATAL, null, e);
    }


}
