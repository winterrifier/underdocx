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
import de.underdocx.enginelayers.baseengine.EngineAccess;
import de.underdocx.enginelayers.baseengine.modifiers.EngineListener;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Regex;

import java.util.Optional;

public class VariableCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> implements EngineListener<C, D> {

    public static final Regex KEYS = new Regex("Pop|Push");

    public static final String KEY_ATTR = "key";
    public static final String VALUE_ATTR = "value";

    public VariableCommandHandler() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        engineAccess.addListener(this);
        return switch (placeholderData.getKey()) {
            case "Pop" -> handlePopCommand();
            case "Push" -> handlePushCommand();
            default -> CommandHandlerResult.IGNORED;
        };
    }

    private CommandHandlerResult handlePushCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED, result ->
                resolveStringAttribute(KEY_ATTR).getOptionalValue().ifPresent(key ->
                        resolveModelValue().ifPresent(modelValue ->
                                modelAccess.pushVariable(key, modelValue))));
    }

    private CommandHandlerResult handlePopCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED, result ->
                resolveStringAttribute(KEY_ATTR).getOptionalValue().ifPresent(key ->
                        modelAccess.popVariable(key)));
    }

    @Override
    public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
        engineAccess.lookBack(node -> {
            Optional<ParametersPlaceholderData> placeholderData = examineNode(node);
            return placeholderData.isPresent() && KEYS.matches(placeholderData.get().getKey());
        }).forEach(placeholderNode -> DeletePlaceholderModifier.modify(placeholderNode, DeletePlaceholderModifierData.DEFAULT));
    }
}
