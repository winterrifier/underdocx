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
import de.underdocx.enginelayers.baseengine.EngineAccess;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.Selection;
import org.w3c.dom.Node;

public class SelectionImpl<C extends DocContainer<D>, P, D> extends SelectedNodeImpl<C, P, D> implements Selection<C, P, D> {

    private final C doc;

    private final EngineAccess engineAccess;

    public SelectionImpl(C doc, Node node, PlaceholdersProvider<C, P, D> provider, EngineAccess engineAccess) {
        super(node, provider);
        this.doc = doc;
        this.engineAccess = engineAccess;
    }

    @Override
    public C getDocContainer() {
        return doc;
    }

    @Override
    public EngineAccess getEngineAccess() {
        return engineAccess;
    }

    @Override
    public String toString() {
        return String.valueOf(getNode());
    }
}
