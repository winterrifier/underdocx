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

package de.underdocx.tools.tree;

import org.w3c.dom.Node;

import java.util.IdentityHashMap;
import java.util.Map;

import static de.underdocx.tools.common.Convenience.buildString;

public class TreePrinter {
    private final Map<Node, String> nodeMarkerMap = new IdentityHashMap<>();
    private final Node tree;
    private boolean noWS = false;

    public TreePrinter(Node tree) {
        this.tree = tree;
    }

    public TreePrinter(Node tree, boolean noWhitespace) {
        this.tree = tree;
        this.noWS = noWhitespace;
    }

    public void addMarker(Node node, String marker) {
        nodeMarkerMap.put(node, marker);
    }

    public String toString() {
        return buildString(builder -> {
            TreeWalker walker = new TreeWalker(tree, tree);
            int intent = 0;
            while (walker.hasNext()) {
                TreeWalker.VisitState state = walker.next();
                if (state.isBeginVisit()) {
                    newLine(builder, intent);
                    if (Nodes.isText(state.getNode())) {
                        builder.append(state.getNode().getNodeValue());
                        checkMarker(builder, state);
                    } else {
                        builder.append("<").append(state.getNode().getNodeName()).append(">");
                        checkMarker(builder, state);
                    }
                    intent++;
                } else {
                    intent--;
                    if (!Nodes.isText(state.getNode())) {
                        newLine(builder, intent);
                        builder.append("</").append(state.getNode().getNodeName()).append(">");
                        checkMarker(builder, state);
                    }
                }
            }
        });
    }

    private void checkMarker(StringBuilder builder, TreeWalker.VisitState state) {
        if (nodeMarkerMap.containsKey(state.getNode())) {
            builder.append("@").append(nodeMarkerMap.get(state.getNode()));
        }
    }

    private void newLine(StringBuilder b, int intent) {
        if (noWS) return;
        b.append("\n");
        b.append("  ".repeat(Math.max(0, intent)));
    }

    public static String getTreeString(Node tree) {
        return new TreePrinter(tree).toString();
    }

    public static void printTree(Node tree) {
        System.out.println(getTreeString(tree));
    }

}
