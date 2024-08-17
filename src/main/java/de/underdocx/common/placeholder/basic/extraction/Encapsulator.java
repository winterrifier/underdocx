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

package de.underdocx.common.placeholder.basic.extraction;

import de.underdocx.common.placeholder.basic.detection.TextDetectionResult;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.nodepath.TextNodePath;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import static de.underdocx.tools.common.Convenience.also;
import static de.underdocx.tools.common.Convenience.first;

public class Encapsulator {

    private final TextDetectionResult.TextArea area;

    private static final Regex WS = new Regex("\\t\\r\\n");

    public static Node encapsulate(TextDetectionResult.TextArea area) {
        return new Encapsulator(area).exec();
    }

    private Encapsulator(TextDetectionResult.TextArea area) {
        this.area = area;
    }

    private TextNodePath getPath() {
        return area.path;
    }

    private TextNodePath.TextRange getRange() {
        return area.range;
    }

    private Node getNode(int index) {
        return getPath().getNodes().get(index);
    }

    private Node getBeginNode() {
        return this.area.getBeginNode();
    }

    private Node getEndNode() {
        return this.area.getEndNode();
    }

    private int getBeginNodeIndex() {
        return this.area.range.getBeginNodeIndex();
    }

    private int getEndNodeIndex() {
        return this.area.range.getEndNodeIndex();
    }

    private int getBeginCharIndex() {
        return this.area.range.getBeginNodeCharIndex();
    }

    private int getEndCharIndex() {
        return this.area.range.getEndNodeCharIndex();
    }

    private TextNodeInterpreter getInterpreter() {
        return area.interpreter;
    }

    private String getBeginNodeValue() {
        return getBeginNode().getNodeValue();
    }

    private void setBeginNodeValue(String value) {
        getBeginNode().setNodeValue(value);
    }

    private String getEndNodeValue() {
        return getEndNode().getNodeValue();
    }

    private void setEndNodeValue(String value) {
        getEndNode().setNodeValue(value);
    }

    private String replaceWS(String str) {
        return WS.replaceAll(str, " ");
    }

    private Node exec() {
        excludeLastText();
        extendFirstText();
        Set<Node> parentNodes = deleteMiddleTextNodes();
        deleteEmptyContainers(parentNodes);
        excludeFirstText();
        return createEncaplsulatedNode();
    }


    private void excludeLastText() {
        if (getEndCharIndex() != getEndNodeValue().length() - 1) {
            Node clone = Nodes.cloneNode(getEndNode(), getEndNode(), false, true);
            clone.setNodeValue(getEndNodeValue().substring(getEndCharIndex() + 1));
            setEndNodeValue(getEndNodeValue().substring(0, getEndCharIndex() + 1));
        }
    }

    private void excludeFirstText() {
        if (getBeginNodeIndex() != 0) {
            Node clone = Nodes.cloneNode(getBeginNode(), getBeginNode(), true, true);
            clone.setNodeValue(getBeginNodeValue().substring(0, getBeginCharIndex()));
            setBeginNodeValue(getBeginNodeValue().substring(getBeginCharIndex()));
        }
    }

    private void extendFirstText() {
        String text = replaceWS(getPath().fetchText(getRange()));
        String nodeText = getBeginCharIndex() > 0 ? getBeginNodeValue().substring(0, getBeginCharIndex()) + text : text;
        setBeginNodeValue(nodeText);
    }

    private Set<Node> deleteMiddleTextNodes() {
        if (getBeginNodeIndex() == getEndNodeIndex()) return Set.of();
        return also(Collections.newSetFromMap(new IdentityHashMap<>()), result -> {
            for (int i = getBeginNodeIndex() + 1; i <= getEndNodeIndex(); i++) {
                Node node = getNode(i);
                if (getInterpreter().isPlainTextType(node)) {
                    result.add(also(node.getParentNode(), parent -> parent.removeChild(node)));
                }
            }
        });
    }

    private void deleteEmptyContainers(Set<Node> parentNodes) {
        Set<Node> workList = also(Collections.newSetFromMap(new IdentityHashMap<>()), set -> set.addAll(parentNodes));
        while (!workList.isEmpty()) {
            Node parent = first(workList);
            workList.remove(parent);
            if (getInterpreter().isPlainTextContainerType(parent) && !parent.hasChildNodes()) {
                workList.add(also(parent.getParentNode(), grandParent -> grandParent.removeChild(parent)));
            }
        }
    }

    private Node createEncaplsulatedNode() {
        Node parent = getBeginNode().getParentNode();
        if (parent.getChildNodes().getLength() == 1 && getInterpreter().isPlainTextContainerType(parent)) return parent;
        return also(getInterpreter().createTextContainer(parent), container -> {
            parent.insertBefore(container, getBeginNode());
            container.appendChild(getBeginNode());
        });
    }
}
