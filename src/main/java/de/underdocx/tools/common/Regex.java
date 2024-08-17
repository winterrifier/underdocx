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

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    private Pattern pattern;

    public Regex(Pattern pattern) {
        this.pattern = pattern;
    }

    public Regex(String string) {
        this(Pattern.compile(string));
    }

    public Optional<Pair<Integer, Integer>> findFirst(String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Optional.of(new Pair<>(matcher.start(), matcher.end() - 1));
        } else {
            return Optional.empty();
        }

    }

    public boolean contains(String text) {
        return findFirst(text).isPresent();
    }

    public boolean matches(String text) {
        return pattern.matcher(text).matches();
    }

    public String replaceAll(String text, String replaceWith) {
        return pattern.matcher(text).replaceAll(replaceWith);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
