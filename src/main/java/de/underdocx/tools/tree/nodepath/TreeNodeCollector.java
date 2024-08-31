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

package de.underdocx.tools.tree.nodepath;

import de.underdocx.tools.tree.TreeWalker;
import de.underdocx.tools.tree.enumerator.Enumerator;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.underdocx.tools.common.Convenience.*;

public class TreeNodeCollector implements Enumerator<Node> {

    public static final Predicate<TreeWalker.VisitState> BeginVisitNodeFilter = TreeWalker.VisitState::isBeginVisit;

    private final Predicate<TreeWalker.VisitState> filter;
    private final List<Node> path;
    private final TreeWalker walker;

    public TreeNodeCollector(Node start, Node limit, List<Node> path, Predicate<TreeWalker.VisitState> filter) {
        this.path = path;
        this.filter = filter;
        this.walker = new TreeWalker(start, limit);
    }

    public TreeNodeCollector(Node start, Node limit, List<Node> path) {
        this(start, limit, path, BeginVisitNodeFilter);
    }

    public TreeNodeCollector(Node start, Node limit) {
        this(start, limit, new ArrayList<>(), BeginVisitNodeFilter);
    }

    private Optional<Node> nextNode(boolean keepCurrentState) {
        TreeWalker treeWalker = keepCurrentState ? new TreeWalker(walker) : walker;
        Optional<TreeWalker.VisitState> next = treeWalker.next(filter);
        return next.map(TreeWalker.VisitState::getNode);
    }

    @Override
    public boolean hasNext() {
        return nextNode(true).isPresent();
    }

    public Node next() {
        return also(nextNode(false), optionalNode -> optionalNode.ifPresent(path::add)).orElse(null);
    }

    public List<Node> getNodes() {
        return path;
    }
}
