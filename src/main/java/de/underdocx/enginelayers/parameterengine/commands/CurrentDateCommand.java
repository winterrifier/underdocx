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

package de.underdocx.enginelayers.parameterengine.commands;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.modifiers.stringmodifier.ReplaceWithTextModifier;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Convenience;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDateCommand<C extends DocContainer<D>, D> implements CommandHandler<C, ParametersPlaceholderData, D> {
    @Override
    public CommandHandlerResult tryExecuteCommand(Selection<C, ParametersPlaceholderData, D> selection) {
        return Convenience.build(CommandHandlerResult.IGNORED, result -> {
            ParametersPlaceholderData placeholderData = selection.getPlaceholderData();
            if (placeholderData.getKey().equals("CurrentDate")) {
                String format = (placeholderData.getStringAttribute("format")).orElse("yyyy-mm-dd");
                String dateText = new SimpleDateFormat(format).format(new Date());
                result.value = CommandHandlerResult.mapToExecuted(new ReplaceWithTextModifier().modify(selection, dateText));
            }
        });
    }
}
