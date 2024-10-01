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

package de.underdocx.enginelayers.modelengine.modelpath.elements;

import de.underdocx.enginelayers.modelengine.model.ModelNode;

import java.util.List;
import java.util.Optional;

import static de.underdocx.tools.common.Convenience.*;

public class IndexModelPathElement implements ModelPathElement {
    private final int index;

    public int getIndex() {
        return index;
    }

    public IndexModelPathElement(int index) {
        this.index = index;
    }

    public String toString() {
        return "[" + index + "]";
    }

    @Override
    public ModelPathElementType getType() {
        return ModelPathElementType.INDEX;
    }

    @Override
    public Optional<ModelNode> interpret(ModelNode node) {
        return buildOptional(w -> w.value = node.hasProperty(index) ? node.getProperty(index) : null);
    }

    @Override
    public void interpret(List<ModelPathElement> elementsWithoutThis) {
        elementsWithoutThis.add(this);
    }
}
