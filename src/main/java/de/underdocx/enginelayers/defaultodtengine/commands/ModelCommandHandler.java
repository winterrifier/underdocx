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
import de.underdocx.tools.common.Regex;

import java.util.Optional;
import java.util.regex.Pattern;

import static de.underdocx.tools.common.Convenience.*;

public class ModelCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D>
        implements EngineListener<C, D> {

    public static final String MODEL = "Model";
    public static final Regex KEYS = new Regex(Pattern.quote(MODEL));

    public ModelCommandHandler() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        return build(CommandHandlerResult.IGNORED,
                result -> {
                    Optional<String> resolvedValue = resolveValue();
                    if (resolvedValue.isPresent()) {
                        modelAccess.setCurrentPath(resolvedValue.get());
                        engineAccess.addListener(this);
                        result.value = CommandHandlerResult.EXECUTED;
                    } else {
                        Optional<String> interpret = resolveStringAttribute("interpret").getOptionalValue();
                        if (interpret.isPresent()) {
                            modelAccess.interpret(interpret.get(), true);
                            result.value = CommandHandlerResult.EXECUTED;
                        }
                    }

                }
        );
    }


    @Override
    public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
        engineAccess.lookBack(node -> {
            Optional<ParametersPlaceholderData> placeholderData = examineNode(node);
            return placeholderData.isPresent() && (placeholderData.get()).getKey().equals(MODEL);
        }).forEach(placeholderNode -> DeletePlaceholderModifier.modify(placeholderNode, DeletePlaceholderModifierData.DEFAULT));
    }


}
