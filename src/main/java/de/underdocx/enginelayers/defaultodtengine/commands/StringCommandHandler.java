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

package de.underdocx.enginelayers.defaultodtengine.commands;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.ReplaceWithTextModifier;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.ResolvedAttributeValue;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Regex;

import java.util.Optional;
import java.util.regex.Pattern;

public class StringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {
    
    public final static Regex KEYS = new Regex(Pattern.quote("String"));

    public StringCommandHandler() {
        super(KEYS);
    }

    protected CommandHandlerResult tryExecuteTextualCommand() {
        ResolvedAttributeValue<String> resolvedText = resolveStringAttribute("value");
        if (resolvedText.getValue() != null) {
            new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, resolvedText.getValue());
            return CommandHandlerResult.EXECUTED;
        }
        if (resolvedText.getResolveType() == ResolvedAttributeValue.ResolveType.UNRESOLVED_NO_ATTRIBUTE) {
            Optional<String> txt = getTextNoAttribute();
            if (txt.isPresent()) {
                new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, txt.get());
                return CommandHandlerResult.EXECUTED;
            }
        }
        DeletePlaceholderModifier.modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        return CommandHandlerResult.EXECUTED;
    }

    private Optional<String> getTextNoAttribute() {
        return Convenience.buildOptional(result -> {
            modelAccess.getCurrentModelNode().ifPresent(modelNode -> {
                if (modelNode.getType() == ModelNode.ModelNodeType.LEAF) {
                    result.value = String.valueOf(modelNode.getValue());
                }
            });
        });
    }
}
