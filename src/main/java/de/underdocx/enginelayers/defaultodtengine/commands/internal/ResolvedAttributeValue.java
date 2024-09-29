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

import java.util.Optional;

public class ResolvedAttributeValue<T> {


    public enum ResolveType {
        UNRESOLVED_NO_ATTRIBUTE,
        UNRESOLVED_NO_MODEL,
        UNRESOLVED_NO_VAR,
        UNRESOLVED_INCOMPATIBLE,
        UNRESOLVED_EMPTY_ATTR,
        RESOLVED_ATTRIBUTE_VALUE,
        RESOLVED_MODEL_VALUE,
        RESOLVED_VAR_VALUE
    }

    private T value = null;
    private ResolveType resolveType;

    public boolean isResolved() {
        return resolveType == ResolveType.RESOLVED_ATTRIBUTE_VALUE
                || resolveType == ResolveType.RESOLVED_VAR_VALUE
                || resolveType == ResolveType.RESOLVED_MODEL_VALUE;
    }

    public ResolvedAttributeValue(ResolveType type, T value) {
        this.resolveType = type;
        this.value = value;
    }

    public ResolveType getResolveType() {
        return resolveType;
    }

    public void setResolveType(ResolveType resolveType) {
        this.resolveType = resolveType;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Optional<T> getOptionalValue() {
        return Optional.ofNullable(value);
    }

}
