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

package de.underdocx.enginelayers.defaultodtengine.commands.internal;

import de.underdocx.enginelayers.modelengine.internal.modelpath.ModelPath;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.Optional;
import java.util.function.Function;

public class AttributeResolver {

    public static Optional<String> tryConvertString(Object value) {
        return Optional.of(String.valueOf(value));
    }

    public static Optional<Boolean> tryConvertBoolean(Object value) {
        if (value instanceof Boolean) return Optional.of((Boolean) value);
        String asString = String.valueOf(value);
        return Optional.of(Boolean.valueOf(asString));
    }

    public static Optional<Integer> tryConvertInteger(Object value) {
        if (value instanceof Integer) return Optional.of((Integer) value);
        String asString = String.valueOf(value);
        try {
            return Optional.of(Integer.valueOf(asString));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> tryConvertDouble(Object value) {
        if (value instanceof Double) return Optional.of((Double) value);
        String asString = String.valueOf(value);
        try {
            return Optional.of(Double.valueOf(asString));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> ResolvedAttributeValue<T> resolveAttribute(String name, ModelNode model, ParametersPlaceholderData placeholderData, Function<String, Optional<T>> attValueGetter, Function<Object, Optional<T>> converter) {
        if (!placeholderData.hasAttribute(name) && !placeholderData.hasAttribute("@" + name)) {
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_ATTRIBUTE, null);
        }
        if (placeholderData.hasAttribute(name)) {
            if (!placeholderData.hasNotNullAttribute(name)) {
                return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.RESOLVED_ATTRIBUTE_VALUE, null);
            }
            Optional<T> attrValue = attValueGetter.apply(name);
            if (attrValue.isPresent()) {
                return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.RESOLVED_ATTRIBUTE_VALUE, attrValue.get());
            }
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.UNRESOLVED_INCOMPATIBLE, null);
        }
        Optional<String> modelPathStr = placeholderData.getStringAttribute("@" + name);
        if (!modelPathStr.isPresent()) {
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.UNRESOLVED_INCOMPATIBLE, null);
        }
        Optional<ModelNode> modelNode = ModelPath.interpret(modelPathStr.get(), model);
        if (!modelNode.isPresent()) {
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_MODEL, null);
        }
        if (modelNode.get().isNull()) {
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.RESOLVED_MODEL_VALUE, null);
        }
        Optional<T> converted = converter.apply(modelNode.get().getValue());
        if (!converted.isPresent()) {
            return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.UNRESOLVED_INCOMPATIBLE, null);
        }
        return new ResolvedAttributeValue(ResolvedAttributeValue.ResolveType.RESOLVED_MODEL_VALUE, converted.get());
    }

    public static ResolvedAttributeValue<String> resolveStringAttribute(String name, ModelNode model, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, model, placeholderData, placeholderData::getStringAttribute, AttributeResolver::tryConvertString);
    }

    public static ResolvedAttributeValue<Boolean> resolveBooleanAttribute(String name, ModelNode model, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, model, placeholderData, placeholderData::getBooleanAttribute, AttributeResolver::tryConvertBoolean);
    }

    public static ResolvedAttributeValue<Integer> resolveIntegerAttribute(String name, ModelNode model, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, model, placeholderData, placeholderData::getIntegerAttribute, AttributeResolver::tryConvertInteger);
    }

    public static ResolvedAttributeValue<Double> resolveDoubleAttribute(String name, ModelNode model, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, model, placeholderData, placeholderData::getDoubleAttribute, AttributeResolver::tryConvertDouble);
    }
}
