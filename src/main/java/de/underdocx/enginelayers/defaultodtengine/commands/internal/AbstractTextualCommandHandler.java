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

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.SelectedNode;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Regex;
import org.w3c.dom.Node;

import java.util.Optional;

public abstract class AbstractTextualCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    protected Regex allowedKeys = null;
    protected TextualPlaceholderToolkit<ParametersPlaceholderData> placeholderToolkit = null;

    protected AbstractTextualCommandHandler() {
    }

    protected AbstractTextualCommandHandler(Regex keys) {
        this.allowedKeys = keys;
    }


    protected ResolvedAttributeValue<String> resolveStringAttribute(String name) {
        return AttributeResolver.resolveStringAttribute(name, modelAccess, placeholderData);
    }

    protected ResolvedAttributeValue<Boolean> resolveBooleanAttribute(String name) {
        return AttributeResolver.resolveBooleanAttribute(name, modelAccess, placeholderData);
    }

    protected ResolvedAttributeValue<Integer> resolveIntegerAttribute(String name) {
        return AttributeResolver.resolveIntegerAttribute(name, modelAccess, placeholderData);
    }

    protected ResolvedAttributeValue<Double> resolveDoubleAttribute(String name) {
        return AttributeResolver.resolveDoubleAttribute(name, modelAccess, placeholderData);
    }

    protected Optional<String> resolveValue() {
        return resolveStringAttribute("value").getOptionalValue();
    }

    protected Optional<ModelNode> resolveModelValue() {
        return AttributeResolver.resolveModelNode("value", modelAccess, placeholderData).getOptionalValue();
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        this.placeholderToolkit = UnderdocxExecutionException.expect(selection.getPlaceholderToolkit());
        if (allowedKeys == null || allowedKeys.matches(placeholderData.getKey())) {
            return tryExecuteTextualCommand();
        } else {
            return CommandHandlerResult.IGNORED;
        }
    }

    protected Optional<ParametersPlaceholderData> examineNode(Node node) {
        return Convenience.buildOptional(w -> {
            if (node.getParentNode() != null && node.getOwnerDocument() != null) {
                String content = node.getTextContent();
                if (content != null) {
                    ParametersPlaceholderCodec.INSTANCE.parse(content).ifPresent(data -> w.value = data);
                }
            }
        });
    }

    protected abstract CommandHandlerResult tryExecuteTextualCommand();

    Optional<SelectedNode<ParametersPlaceholderData>> findEndNode(String endKey) {
        Optional<Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>>> pair = AreaTools.findArea(selection.getEngineAccess().lookAhead(null), selection, endKey);
        if (pair.isPresent()) {
            return Optional.of(pair.get().right);
        } else {
            return Optional.of(null);
        }
    }
}
