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

package de.underdocx.enginelayers.baseengine.commands;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholderData;
import de.underdocx.enginelayers.baseengine.modifiers.existingimage.ExistingImageModifier;
import de.underdocx.enginelayers.baseengine.modifiers.existingimage.ExistingImageModifierData;

import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import static de.underdocx.tools.common.Convenience.build;

public class SimpleDollarImageReplaceCommand<C extends DocContainer<D>, D> implements CommandHandler<C, SimpleDollarImagePlaceholderData, D> {

    private final Function<String, Optional<URL>> function;
    private final boolean keepWidth;

    public SimpleDollarImageReplaceCommand(Function<String, Optional<URL>> function, boolean keepWidth) {
        this.function = function;
        this.keepWidth = keepWidth;
    }

    @Override
    public CommandHandlerResult tryExecuteCommand(Selection<C, SimpleDollarImagePlaceholderData, D> selection) {
        return build(CommandHandlerResult.IGNORED, result ->
                function.apply(selection.getPlaceholderData().getVariableName()).ifPresent(url -> {
                    result.value = CommandHandlerResult.EXECUTED;
                    new ExistingImageModifier<C, D>().modify(selection, new ExistingImageModifierData.Simple(keepWidth, url));
                }));
    }
}
