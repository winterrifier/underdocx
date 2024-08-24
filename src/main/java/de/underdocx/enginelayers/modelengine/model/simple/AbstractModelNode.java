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

import de.underdocx.common.codec.JsonCodec;
import de.underdocx.enginelayers.modelengine.model.ModelNode;

import java.util.*;

import static de.underdocx.tools.common.Convenience.also;
import static de.underdocx.tools.common.Convenience.build;

public abstract class AbstractModelNode<T> implements ModelNode {

    protected static final JsonCodec jsonCodec = new JsonCodec(true, true, true);

    protected T containedValue;
    protected AbstractModelNode<?> parent;

    protected void setParent(AbstractModelNode<?> node) {
        if (this.parent != null && this.parent != node) {
            this.parent.removeChild(node);
        }
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

    @Override
    public ModelNode getProperty(String name) {
        return null;
    }

    @Override
    public ModelNode getProperty(int index) {
        return null;
    }

    @Override
    public ModelNode getParent() {
        return parent;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean hasProperty(String name) {
        return false;
    }

    @Override
    public boolean hasProperty(int index) {
        return false;
    }

    @Override
    public boolean isNull() {
        return getValue() != null;
    }

    public static final AbstractModelNode create(Object object) {
        return build(w -> {
            if (object == null) {
                w.value = new LeafModelNode(object);
            } else if (object instanceof Collection<?>) {
                w.value = also(new ListModelNode(), node -> {
                    ((List<?>) object).forEach(child -> {
                        node.add(create(child));
                    });
                });
            } else if (object instanceof Map<?, ?>) {
                w.value = also(new MapModelNode(), node -> {
                    ((Map<?, ?>) object).forEach((key, value) -> {
                        node.add(String.valueOf(key), create(value));
                    });
                });
            } else {
                w.value = new LeafModelNode(object);
            }
        });
    }

    protected static Object convert(AbstractModelNode<?> node) {
        if (node.getValue() == null) {
            return null;
        } else if (node instanceof MapModelNode) {
            return also(new HashMap<String, Object>(),
                    map -> ((Map<String, AbstractModelNode>) node.getValue()).forEach(
                            (key, value) -> map.put(key, convert(value))));
        } else if (node instanceof ListModelNode) {
            return also(new ArrayList<>(),
                    list -> ((List<AbstractModelNode>) node.getValue()).forEach(
                            value -> list.add(convert(value))));
        } else {
            return node.getValue();
        }
    }

    public String toString() {
        return jsonCodec.convertMapToJsonString(convert(this)).orElse("ModelNode: " + containedValue);
    }
}
