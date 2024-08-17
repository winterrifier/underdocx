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

import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Wrapper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

import static de.underdocx.tools.common.Convenience.*;

/**
 * Collection of helper methods to make W3C API more convenient
 */
public class Nodes {

    /*

      -- Collections --

     */

    public static List<Node> toList(NodeList nodeList) {
        return buildList(result -> {
            for (int i = 0; i < nodeList.getLength(); i++) {
                result.add(nodeList.item(i));
            }
        });
    }

    public static List<Node> children(Node node) {
        return toList(node.getChildNodes());
    }

    public static Map<String, String> attributes(Node node) {
        NamedNodeMap mapToConvert = node.getAttributes();
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < mapToConvert.getLength(); i++) {
            result.put(mapToConvert.item(i).getNodeName(), mapToConvert.item(i).getNodeValue());
        }
        return result;
    }

    /*

      -- search nodes --

     */

    public static List<Node> findDescendantNodes(Node start, String tagName) {
        return buildList(result -> {
            if (start.getNodeName().equals(tagName)) {
                result.add(start);
            }
            children(start).forEach(element -> result.addAll(findDescendantNodes(element, tagName)));
        });
    }


    public static Optional<Node> findFirstDescendantNode(Node start, String tagName) {
        return (start.getNodeName().equals(tagName))
                ? Optional.of(start)
                : findFirstPresent(children(start), child -> findFirstDescendantNode(child, tagName));
    }

    public static Optional<Node> findAscendantNode(Node start, String tagName) {
        if (start.getNodeName().equals(tagName)) {
            return Optional.of(start);
        } else {
            Node parent = start.getParentNode();
            return parent == null ? Optional.empty() : findAscendantNode(parent, tagName);
        }
    }

    public static List<Node> getAncestors(Node node, Node limit) {
        ArrayList<Node> result = new ArrayList<>();
        if (node == limit) {
            return add(result, node);
        }
        Node parent = node;
        while (parent != null && parent != limit) {
            parent = push(result, parent).getParentNode();
        }
        if (parent != null) return add(result, parent);
        return result;
    }

    public static List<Node> getSiblings(Node firstNode, Node limit) {
        return buildList(result -> {
            Node current = null;
            do {
                current = current == null ? firstNode : current.getNextSibling();
                if (current != null) {
                    result.add(current);
                }
            } while (current != null && current != limit);
        });
    }

    public static int compareNodePositions(Node node1, Node node2) {
        List<Node> path1 = reverse(getAncestors(node1, null));
        List<Node> path2 = reverse(getAncestors(node2, null));
        for (int treeDepth = 0; treeDepth < path1.size(); treeDepth++) {
            if (treeDepth >= path2.size()) {
                return -1; // a->b*->c  vs  a->b*
            }
            if (path1.get(treeDepth) == path2.get(treeDepth)) {
                continue; // a->b*->c  vs  a->b*->x
            }
            Node child1 = path1.get(treeDepth);
            Node child2 = path2.get(treeDepth);
            while (child1 != null) {
                child1 = child1.getNextSibling();
                if (child1 == child2) {
                    return -1;
                }
            }
            return 1;
        }
        return path1.size() == path2.size()
                ? 0  // a->b*  vs   a->b*
                : 1; // a->b*  vs   a->b*->c
    }




    /*

      -- insert/reorder/delete nodes --

     */

    public static void insertNodes(Node refNode, List<Node> toInsert, boolean insertBefore) {
        if (insertBefore) {
            final Wrapper<Node> last = new Wrapper<>(refNode);
            reverse(toInsert).forEach(element -> {
                refNode.getParentNode().insertBefore(element, last.value);
                last.value = element;
            });
        } else {
            reverse(toInsert).forEach(element -> {
                if (refNode.getNextSibling() == null) {
                    refNode.getParentNode().appendChild(element);
                } else {
                    refNode.getParentNode().insertBefore(element, refNode.getNextSibling());
                }
            });
        }
    }

    public static void insertNode(Node refNode, Node toInsert, boolean insertBefore) {
        insertNodes(refNode, Collections.singletonList(toInsert), insertBefore);
    }

    public static void insertBefore(Node refNode, Node toInsert) {
        insertNode(refNode, toInsert, true);
    }

    public static void insertAfter(Node refNode, Node toInsert) {
        insertNode(refNode, toInsert, false);
    }

    public static void insertBefore(Node refNode, List<Node> toInsert) {
        insertNodes(refNode, toInsert, true);
    }

    public static void insertAfter(Node refNode, List<Node> toInsert) {
        insertNodes(refNode, toInsert, false);
    }

    public static void addChildren(Node parent, List<Node> toInsert, boolean atBegin) {
        if (parent.hasChildNodes() && atBegin) {
            insertNodes(parent.getFirstChild(), toInsert, true);
        } else {
            new ArrayList<>(toInsert).forEach(parent::appendChild);
        }
    }

    public static void deleteNodes(Collection<Node> nodes) {
        nodes.forEach(element -> element.getParentNode().removeChild(element));
    }

    public static void deleteChildren(Node node) {
        deleteNodes(children(node));
    }

    public static void setNodeText(Node node, String text) {
        deleteChildren(node);
        node.appendChild(node.getOwnerDocument().createTextNode(text));
    }





    /*

      -- clone/import nodes --

     */

    public static Node importNode(Node parent, Node toImport) {
        Node result = parent.getOwnerDocument().adoptNode(toImport.cloneNode(true));
        parent.appendChild(result);
        return result;
    }

    public static List<Node> importNodes(Node parent, Collection<Node> toImport) {
        return buildList(result -> toImport.forEach(element -> result.add(importNode(parent, element))));
    }

    public static Node cloneNode(Node refNode, Node toClone, boolean insertBefore, boolean deepClone) {
        Node clone = toClone.cloneNode(deepClone);
        insertNode(refNode, clone, insertBefore);
        return clone;
    }

    public static List<Node> cloneNodes(Node refNode, List<Node> toClone, boolean insertBefore, boolean deepClone) {
        return buildList(result -> {
            toClone.forEach(element -> result.add(cloneNode(refNode, element, false, deepClone)));
            insertNodes(refNode, result, insertBefore);
        });

    }

    public static List<Node> cloneChildren(Node parentNode, List<Node> toClone, boolean atBegin, boolean deepClone) {
        return buildList(result -> {
            toClone.forEach(element -> result.add(cloneNode(parentNode, element, false, deepClone)));
            addChildren(parentNode, result, atBegin);
        });
    }


    /*

    -- more helpers

     */
    public static boolean isText(Node... nodes) {
        return Convenience.all(Arrays.asList(nodes), node -> node.getNodeType() == Node.TEXT_NODE);
    }


}

