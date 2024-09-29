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

package de.underdocx.enginelayers.modelengine.internal;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.EngineAccess;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.modelengine.MSelection;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;
import org.w3c.dom.Node;

import java.util.Optional;

public class MSelectionWrapper<C extends DocContainer<D>, P, D> implements MSelection<C, P, D> {

    private final Selection<C, P, D> selection;
    private final ModelAccess access;

    public MSelectionWrapper(Selection<C, P, D> selection, ModelAccess access) {
        this.selection = selection;
        this.access = access;
    }

    public MSelectionWrapper(Selection<C, P, D> selection) {
        this(selection, null);
    }

    @Override
    public Optional<ModelAccess> getModelAccess() {
        return Optional.ofNullable(access);
    }

    @Override
    public Node getNode() {
        return selection.getNode();
    }

    @Override
    public C getDocContainer() {
        return selection.getDocContainer();
    }

    @Override
    public P getPlaceholderData() {
        return selection.getPlaceholderData();
    }

    @Override
    public Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit() {
        return selection.getPlaceholderToolkit();
    }

    @Override
    public EngineAccess<C, D> getEngineAccess() {
        return selection.getEngineAccess();
    }

    @Override
    public String toString() {
        return String.valueOf(getNode());
    }
}
