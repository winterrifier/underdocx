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

package de.underdocx.enginelayers.baseengine;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.Enumerator;
import de.underdocx.tools.tree.Nodes;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.underdocx.tools.common.Convenience.also;

public class PlaceholdersEnumerator<C extends DocContainer<D>, D> implements Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> {

    private final Map<PlaceholdersProvider<C, ?, D>, Enumerator<Node>> currentEnumerators = new HashMap<>();
    private final Map<PlaceholdersProvider<C, ?, D>, Node> currentNodes = new HashMap<>();
    private Pair<PlaceholdersProvider<C, ?, D>, Node> next = null;


    public PlaceholdersEnumerator(Set<PlaceholdersProvider<C, ?, D>> providers) {
        providers.forEach(provider -> currentEnumerators.put(provider, provider.getPlaceholders()));
        currentEnumerators.forEach((provider, enumerator) -> {
            Node node = enumerator.next();
            if (node != null && (next == null || Nodes.compareNodePositions(node, next.right) < 0)) {
                next = new Pair<>(provider, node);
            }
            currentNodes.put(provider, node);
        });
    }

    private void findNext() {
        if (next == null) return;
        Node newElement = currentEnumerators.get(next.left).next();
        currentNodes.put(next.left, newElement);
        next = null;

        currentNodes.forEach((provider, node) -> {
            if (node != null && (next == null || Nodes.compareNodePositions(node, next.right) < 0)) {
                next = new Pair<>(provider, node);
            }
        });
    }

    public boolean hasNext() {
        return next != null;
    }

    public Pair<PlaceholdersProvider<C, ?, D>, Node> next() {
        return also(next, n -> findNext());
    }
}
