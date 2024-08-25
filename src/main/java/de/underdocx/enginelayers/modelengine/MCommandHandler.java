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

package de.underdocx.enginelayers.modelengine;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.modelengine.internal.MSelectionWrapper;

public interface MCommandHandler<C extends DocContainer<D>, P, D> extends CommandHandler<C, P, D> {

    default CommandHandlerResult tryExecuteCommand(Selection<C, P, D> selection) {
        return (selection instanceof MSelection<C, P, D>)
                ? tryExecuteCommand((MSelection<C, P, D>) selection)
                : tryExecuteCommand(new MSelectionWrapper<>(selection));
    }

    CommandHandlerResult tryExecuteCommand(MSelection<C, P, D> selection);

}
