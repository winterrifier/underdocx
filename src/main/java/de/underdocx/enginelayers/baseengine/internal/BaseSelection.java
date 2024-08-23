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

package de.underdocx.enginelayers.baseengine.internal;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.Selection;
import org.w3c.dom.Node;

import java.util.Optional;

public class BaseSelection<C extends DocContainer<D>, P, D> implements Selection<C, P, D> {

    private final C doc;
    private final Node node;
    private final P placeholderData;
    private final TextualPlaceholderToolkit<P> toolkit;

    public BaseSelection(C doc, Node node, P placeholderData, TextualPlaceholderToolkit<P> optionalToolkit) {
        this.doc = doc;
        this.node = node;
        this.placeholderData = placeholderData;
        this.toolkit = optionalToolkit;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public C getDocContainer() {
        return doc;
    }

    @Override
    public P getPlaceholderData() {
        return placeholderData;
    }

    @Override
    public Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit() {
        return Optional.ofNullable(toolkit);
    }
}
