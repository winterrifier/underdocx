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

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.codec.JsonCodec;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Pair;

import java.util.Optional;
import java.util.function.Function;

public class AttributeResolver {

    public enum AccessType {
        MODEL("@"),
        VAR("$"),
        ATTR("");

        private final String prefix;

        AccessType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        public static AccessType getTypeOf(String name) {
            if (name.startsWith(MODEL.prefix)) {
                return MODEL;
            }
            if (name.startsWith(VAR.prefix)) {
                return VAR;
            }
            return ATTR;
        }

        public String rename(String propertyName) {
            String result = propertyName;
            if (propertyName.startsWith(MODEL.prefix) || propertyName.startsWith(VAR.prefix)) {
                result = result.substring(1);
            }
            result = prefix + result;
            return result;
        }
    }

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

    public static <T> Optional<T> tryConvert(Object value) {
        try {
            return Optional.of((T) value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static ResolvedAttributeValue<ModelNode> resolveModelNode(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        Optional<AccessType> accessType = getAccessType(name, placeholderData);
        if (accessType.isEmpty()) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_ATTRIBUTE, null);
        }

        return switch (accessType.get()) {
            case ATTR -> resolveNodeFromAttr(name, placeholderData);
            case MODEL -> resolveNodeFromModel(name, modelAccess, placeholderData);
            case VAR -> resolveNodeFromVar(name, modelAccess, placeholderData);
        };
    }

    private static ResolvedAttributeValue<ModelNode> resolveNodeFromVar(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        Optional<String> variableName = placeholderData.getStringAttribute(AccessType.VAR.rename(name));
        if (variableName.isEmpty()) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.UNRESOLVED_EMPTY_ATTR, null);
        }
        Optional<ModelNode> variable = modelAccess.getVariable(variableName.get());
        if (variable.isEmpty()) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_VAR, null);
        }
        return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.RESOLVED_VAR_VALUE, variable.get());
    }

    private static ResolvedAttributeValue<ModelNode> resolveNodeFromAttr(String name, ParametersPlaceholderData placeholderData) {
        JsonNode json = placeholderData.getJson();
        if (!placeholderData.hasNotNullAttribute(name)) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.RESOLVED_ATTRIBUTE_VALUE, new LeafModelNode<>(null));
        }
        JsonNode child = json.get(name);
        return Convenience.build(new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.RESOLVED_MODEL_VALUE, null), result -> {
            if (child.isDouble()) {
                result.value.setValue(new LeafModelNode<>(child.asDouble()));
            } else if (child.isBoolean()) {
                result.value.setValue(new LeafModelNode<>(child.asBoolean()));
            } else if (child.isTextual()) {
                result.value.setValue(new LeafModelNode<>(child.asText()));
            } else if (child.isInt()) {
                result.value.setValue(new LeafModelNode<>(child.asInt()));
            } else if (child.isArray()) {
                result.value.setValue(new MapModelNode(new JsonCodec().getAsList(child)));
            } else {
                result.value.setValue(new MapModelNode(new JsonCodec().getAsMap(child)));
            }
        });
    }

    private static ResolvedAttributeValue<ModelNode> resolveNodeFromModel(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        Optional<String> modelName = placeholderData.getStringAttribute(AccessType.MODEL.rename(name));
        Pair<String, Optional<ModelNode>> modelNode = modelAccess.interpret(modelName.get(), false);
        if (modelNode.right.isEmpty()) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_MODEL, null);
        }
        return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.RESOLVED_MODEL_VALUE, modelNode.right.get());
    }


    private static Optional<AccessType> getAccessType(String name, ParametersPlaceholderData placeholderData) {
        AccessType accessType = null;
        if (placeholderData.hasAttribute(AccessType.MODEL.rename(name))) {
            accessType = AccessType.MODEL;
        } else if (placeholderData.hasAttribute(AccessType.VAR.rename(name))) {
            accessType = AccessType.VAR;
        } else if (placeholderData.hasAttribute(name)) {
            accessType = AccessType.ATTR;
        }
        return Optional.ofNullable(accessType);
    }

    public static <T> ResolvedAttributeValue<T> resolveAttribute(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData, Function<String, Optional<T>> attValueGetter, Function<Object, Optional<T>> converter) {
        ResolvedAttributeValue<ModelNode> resolvedModelNode = resolveModelNode(name, modelAccess, placeholderData);
        if (resolvedModelNode.getValue() == null) {
            return new ResolvedAttributeValue<>(resolvedModelNode.getResolveType(), null);
        }
        ModelNode node = resolvedModelNode.getValue();
        Optional<T> converted = converter.apply(node.getValue());
        if (converted.isEmpty()) {
            return new ResolvedAttributeValue<>(ResolvedAttributeValue.ResolveType.UNRESOLVED_INCOMPATIBLE, null);
        }
        return new ResolvedAttributeValue<>(resolvedModelNode.getResolveType(), converted.get());
    }

    public static ResolvedAttributeValue<String> resolveStringAttribute(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, modelAccess, placeholderData, placeholderData::getStringAttribute, AttributeResolver::tryConvertString);
    }

    public static ResolvedAttributeValue<Boolean> resolveBooleanAttribute(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, modelAccess, placeholderData, placeholderData::getBooleanAttribute, AttributeResolver::tryConvertBoolean);
    }

    public static ResolvedAttributeValue<Integer> resolveIntegerAttribute(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, modelAccess, placeholderData, placeholderData::getIntegerAttribute, AttributeResolver::tryConvertInteger);
    }

    public static ResolvedAttributeValue<Double> resolveDoubleAttribute(String name, ModelAccess modelAccess, ParametersPlaceholderData placeholderData) {
        return resolveAttribute(name, modelAccess, placeholderData, placeholderData::getDoubleAttribute, AttributeResolver::tryConvertDouble);
    }
}
