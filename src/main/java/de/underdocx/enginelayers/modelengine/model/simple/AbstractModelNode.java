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

package de.underdocx.enginelayers.modelengine.model.simple;

import de.underdocx.enginelayers.modelengine.model.ModelNode;

public abstract class AbstractModelNode<T> implements ModelNode {

    protected T containedValue;
    protected AbstractModelNode<?> parent;

    protected void setParent(AbstractModelNode<?> node) {
        if (node != null && this.parent != null && this.parent != node) {
            this.parent.removeChild(node);
        }
        this.parent = node;
    }

    protected abstract void removeChild(AbstractModelNode<?> node);

    protected void checkParentOfChild(AbstractModelNode<?> node) {
        if (node.parent != null && node.parent != this) {
            node.setParent(null);
        }
    }

    @Override
    public T getValue() {
        return containedValue;
    }

    abstract protected AbstractModelNode<?> create(Object object);
}
