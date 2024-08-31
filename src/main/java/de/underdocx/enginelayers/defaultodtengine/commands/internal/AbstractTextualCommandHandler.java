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

import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Convenience;
import org.w3c.dom.Node;

import java.util.Optional;

public abstract class AbstractTextualCommandHandler extends AbstractCommandHandler<ParametersPlaceholderData> {
    protected TextualPlaceholderToolkit<ParametersPlaceholderData> placeholderToolkit = null;

    protected ResolvedAttributeValue<String> resolveStringAttribute(String name) {
        return AttributeResolver.resolveStringAttribute(name, model, placeholderData);
    }

    protected ResolvedAttributeValue<Boolean> resolveBooleanAttribute(String name) {
        return AttributeResolver.resolveBooleanAttribute(name, model, placeholderData);
    }

    protected ResolvedAttributeValue<Integer> resolveIntegerAttribute(String name) {
        return AttributeResolver.resolveIntegerAttribute(name, model, placeholderData);
    }

    protected ResolvedAttributeValue<Double> resolveDoubleAttribute(String name) {
        return AttributeResolver.resolveDoubleAttribute(name, model, placeholderData);
    }

    protected Optional<String> resolveValue() {
        return resolveStringAttribute("value").getOptionalValue();
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        this.placeholderToolkit = UnderdocxExecutionException.expect(selection.getPlaceholderToolkit());
        return tryExecuteTextualCommand();
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
}
