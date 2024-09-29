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

package de.underdocx.enginelayers.baseengine.internal;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.EngineAccess;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.SelectedNode;
import de.underdocx.enginelayers.baseengine.modifiers.EngineListener;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.enumerator.LookAheadEnumerator;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static de.underdocx.tools.common.Convenience.*;

public class EngineAccessImpl<C extends DocContainer<D>, D> implements EngineAccess<C, D> {

    private final Collection<EngineListener<C, D>> listeners;
    private final Runnable rescan;
    private final List<Node> lookBack;
    private final LookAheadEnumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> lookAhead;


    public EngineAccessImpl(Collection<EngineListener<C, D>> listeners, Runnable forceRescan, LookAheadEnumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> lookAhead, List<Node> lookBack) {
        this.listeners = listeners;
        this.rescan = forceRescan;
        this.lookAhead = lookAhead;
        this.lookBack = lookBack;

    }

    @Override
    public void addListener(EngineListener<C, D> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EngineListener<C, D> listener) {
        listeners.remove(listener);
    }

    @Override
    public void forceRescan() {
        rescan.run();
    }

    public List<SelectedNode<?>> lookAhead(Predicate<SelectedNode<?>> filter) {
        return buildList(result -> lookAhead.lookAhead().forEach(pair -> {
            SelectedNodeImpl<C, ?, D> selectedNode = new SelectedNodeImpl<>(pair.right, pair.left);
            if (filter == null || filter.test(selectedNode)) {
                result.add(selectedNode);
            }
        }));
    }

    public List<Node> lookBack(Predicate<Node> filter) {
        return filter(lookBack, filter);
    }
}
