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

package de.underdocx.tools.tree.enumerator;

import java.util.ArrayList;
import java.util.List;

public class SimpleLookAheadEnumerator<T> implements LookAheadEnumerator<T> {

    private final List<T> waiting = new ArrayList<>();

    private final Enumerator<T> inner;

    public SimpleLookAheadEnumerator(Enumerator<T> inner) {
        this.inner = inner;
    }


    @Override
    public boolean hasNext() {
        if (waiting.isEmpty()) {
            return inner.hasNext();
        } else {
            return waiting.size() > 0;
        }
    }

    @Override
    public T next() {
        if (waiting.isEmpty()) {
            return inner.next();
        } else {
            return waiting.remove(0);
        }
    }

    public List<T> lookAhead() {
        waiting.addAll(inner.collect());
        return new ArrayList<>(waiting);
    }

    public List<T> lookAhead(int count) {
        for (int i = 1; i < count; i++) {
            if (i > waiting.size()) {
                if (inner.hasNext()) {
                    waiting.add(inner.next());
                }
            }
        }
        return new ArrayList<>(waiting);
    }
}
