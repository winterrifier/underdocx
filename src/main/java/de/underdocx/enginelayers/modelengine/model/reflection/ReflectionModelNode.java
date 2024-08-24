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

package de.underdocx.enginelayers.modelengine.model.reflection;

import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.environment.UnderdocxEnv;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static de.underdocx.tools.common.Convenience.*;

public class ReflectionModelNode implements ModelNode {

    private final Object containedValue;
    protected ModelNode parent;

    public ReflectionModelNode(Object object) {
        this.containedValue = object;
        this.parent = null;
    }

    protected ReflectionModelNode create(Object object) {
        return also(new ReflectionModelNode(object), result -> result.parent = this);
    }

    @Override
    public ModelNodeType getType() {
        if (containedValue == null) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Collection<?>) {
            return ModelNodeType.LIST;
        } else if (containedValue instanceof Map<?, ?>) {
            return ModelNodeType.MAP;
        } else if (containedValue instanceof Boolean) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Integer) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Long) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Double) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Float) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof String) {
            return ModelNodeType.LEAF;
        } else {
            return ModelNodeType.MAP;
        }
    }

    @Override
    public Object getValue() {
        return containedValue;
    }

    @Override
    public ModelNode getProperty(String name) {
        if (getType() != ModelNodeType.MAP) {
            return null;
        } else if (getValue() instanceof Map<?, ?>) {
            return create(((Map<?, ?>) containedValue).get(name));
        } else {
            return build(
                    w -> findField(name).ifPresentOrElse(
                            field -> readField(field).ifPresent(
                                    result -> w.value = result),
                            () -> findGetMethod(name).ifPresent(
                                    method -> callGetMethod(method).ifPresent(
                                            result -> w.value = result))));
        }
    }

    @Override
    public ModelNode getProperty(int index) {
        if (getType() != ModelNodeType.LIST) {
            return null;
        } else if (getValue() instanceof List<?>) {
            List<?> list = ((List<?>) containedValue);
            if (index >= list.size()) {
                return null;
            }
            return create(((List<?>) containedValue).get(index));
        } else {
            return null;
        }
    }

    @Override
    public ModelNode getParent() {
        return parent;
    }

    @Override
    public int getSize() {
        if (getValue() instanceof List<?>) {
            return ((List<?>) containedValue).size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasProperty(String name) {
        if (getType() != ModelNodeType.MAP) {
            return false;
        } else if (getValue() instanceof Map<?, ?>) {
            return ((Map<?, ?>) containedValue).containsKey(name);
        } else {
            return findGetMethod(name).isPresent() || findField(name).isPresent();
        }
    }

    @Override
    public boolean hasProperty(int index) {
        if (getType() != ModelNodeType.LIST) {
            return false;
        } else if (getValue() instanceof List<?>) {
            return ((List<?>) containedValue).size() > index;
        } else {
            return false;
        }
    }

    @Override
    public boolean isNull() {
        return containedValue == null;
    }

    protected Optional<ReflectionModelNode> callGetMethod(Method method) {
        Object result;
        try {
            method.setAccessible(true);
            result = method.invoke(containedValue);
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.warn(e);
            return Optional.empty();
        }
        return Optional.of(create(result));
    }

    protected Optional<Method> findGetMethod(String name) {
        return findFirst(Arrays.asList(containedValue.getClass().getMethods()), method -> {
            boolean nameFits = method.getName().toLowerCase().equals(name.toLowerCase()) ||
                    method.getName().toLowerCase().equals(("get" + name).toLowerCase()) ||
                    method.getName().toLowerCase().equals(("is" + name).toLowerCase());
            boolean returnFits = method.getReturnType() != Void.class;
            boolean parametersFits = method.getParameterTypes().length == 0;
            return nameFits && returnFits && parametersFits;
        });
    }

    protected Optional<ReflectionModelNode> readField(Field field) {
        Object result;
        try {
            field.setAccessible(true);
            result = field.get(containedValue);
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.warn(e);
            return Optional.empty();
        }
        return Optional.of(create(result));
    }

    protected Optional<Field> findField(String name) {
        return findFirst(Arrays.asList(containedValue.getClass().getFields()),
                field -> field.getName().toLowerCase().equals(name.toLowerCase()) ||
                        field.getName().toLowerCase().equals(("is" + name).toLowerCase()));
    }

    public String toString() {
        return "ReflectionModelNode for: " + containedValue;
    }

}
