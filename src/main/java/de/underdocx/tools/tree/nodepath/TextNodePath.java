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

import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.underdocx.tools.common.Convenience.*;

public class TextNodePath {
    private final TextNodeInterpreter textNodeInterpreter;
    private final List<Node> path;

    public TextNodePath(TextNodeInterpreter textInterpreter) {
        this(new ArrayList<>(), textInterpreter);
    }

    public TextNodePath(List<Node> nodes, TextNodeInterpreter textNodeInterpreter) {
        this.path = nodes;
        this.textNodeInterpreter = textNodeInterpreter;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(path);
    }

    public TextNodeInterpreter getTextNodeInterpreter() {
        return textNodeInterpreter;
    }

    public void addNode(Node node) {
        this.path.add(node);
    }

    public Optional<String> getTextContent() {
        StringBuilder result = new StringBuilder();
        for (Node node : path) {
            String text = textNodeInterpreter.getText(node);
            if (text == null) {
                return Optional.empty();
            }
            result.append(text);
        }
        return Optional.of(result.toString());
    }

    public String fetchTextContent() {
        return buildString(result -> path.forEach(node -> result.append(getOrDefault(textNodeInterpreter.getText(node), ""))));
    }

    public boolean isTextOnly() {
        return all(path, textNodeInterpreter::isTextRelatedType);
    }

    public Optional<String> getNodeText(int index) {
        return Optional.ofNullable(textNodeInterpreter.getText(path.get(index)));
    }

    public String fetchNodeText(int index) {
        return getNodeText(index).orElse("");
    }

    public TextPointer getTextPointer(int charIndex) {
        String nodeText = null;
        StringBuilder collectedText = new StringBuilder();
        int nodeIndex = 0;
        while (nodeIndex < path.size() && collectedText.length() - 1 < charIndex) {
            nodeText = fetchNodeText(nodeIndex);
            collectedText.append(nodeText);
            nodeIndex++;
        }
        if (nodeText == null || nodeIndex - 1 >= path.size()) return null;
        int nodeCharIndex = nodeText.length() - (collectedText.length() - charIndex);
        return new TextPointer(nodeIndex - 1, nodeCharIndex);
    }

    public String fetchText(TextRange range) {
        int bIndex = range.getBeginNodeIndex();
        int eIndex = range.getEndNodeIndex();
        if (bIndex == eIndex) {
            return fetchNodeText(bIndex).substring(range.getBeginNodeCharIndex(), range.getEndNodeCharIndex() + 1);
        }
        return buildString(buffer -> {
            for (int nodeIndex = bIndex; nodeIndex <= eIndex; nodeIndex++) {
                String text = fetchNodeText(nodeIndex);
                if (nodeIndex == bIndex) {
                    text = text.substring(range.getBeginNodeCharIndex());
                } else if (nodeIndex == eIndex) {
                    text = text.substring(0, range.getEndNodeCharIndex() + 1);
                }
                buffer.append(text);
            }
        });
    }

    public static class TextPointer {
        public final int nodeIndex;
        public final int charIndex;

        public TextPointer(int nodeIndex, int charIndex) {
            this.nodeIndex = nodeIndex;
            this.charIndex = charIndex;
        }
    }

    public static class TextRange {
        public final TextPointer begin;
        public final TextPointer end;

        public TextRange(TextPointer begin, TextPointer end) {
            this.begin = begin;
            this.end = end;
        }

        public int getBeginNodeIndex() {
            return begin.nodeIndex;
        }

        public int getEndNodeIndex() {
            return end.nodeIndex;
        }

        public int getBeginNodeCharIndex() {
            return begin.charIndex;
        }

        public int getEndNodeCharIndex() {
            return end.charIndex;
        }


    }

    public String toString() {
        return buildString(r -> path.forEach(node -> {
            if (textNodeInterpreter.isPlainTextType(node)) {
                r.append("(").append(textNodeInterpreter.getText(node)).append(")");
            } else if (textNodeInterpreter.isTextRelatedType(node)) {
                r.append("()");
            } else {
                r.append("<!>");
            }
        }));
    }

}
