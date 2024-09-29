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

import de.underdocx.enginelayers.baseengine.SelectedNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AreaTools {

    public static Optional<Pair<SelectedNode<?>, SelectedNode<?>>>
    findArea(List<SelectedNode<?>> futureNodes,
             SelectedNode<?> startArea,
             Predicate<SelectedNode<?>> beginningNodeFilter,
             Predicate<SelectedNode<?>> endingNodeFilter) {
        int counter = 0;
        for (SelectedNode<?> pNode : futureNodes) {
            if (beginningNodeFilter.test(pNode)) {
                counter++;
            } else if (endingNodeFilter.test(pNode)) {
                counter--;
                if (counter <= 0) {
                    return Optional.of(new Pair<>(startArea, pNode));
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>>>
    findArea(List<SelectedNode<?>> futureNodes,
             SelectedNode<ParametersPlaceholderData> start,
             String endKey) {
        String startKey = start.getPlaceholderData().getKey();
        Predicate<SelectedNode<?>> beginFilter = (s -> s.getPlaceholderData() instanceof ParametersPlaceholderData
                && ((ParametersPlaceholderData) s.getPlaceholderData()).getKey().equals(startKey));
        Predicate<SelectedNode<?>> endFilter = (s -> s.getPlaceholderData() instanceof ParametersPlaceholderData
                && ((ParametersPlaceholderData) s.getPlaceholderData()).getKey().equals(endKey));
        Optional<Pair<SelectedNode<?>, SelectedNode<?>>> result = findArea(futureNodes, start, beginFilter, endFilter);
        return (Optional<Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>>>) ((Object) result);
    }
}
