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

package de.underdocx.tools.odf;

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.tools.tree.enumerator.Enumerator;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ParagraphByParagraphNodesEnumerator implements Enumerator<Node> {
    private final ParagraphWalker walker;
    private final List<Node> collectedNodes = new ArrayList<>();
    private Node next = null;
    private Function<TextParagraphElementBase, Collection<Node>> nodesProvider = null;

    public ParagraphByParagraphNodesEnumerator(OdfContainer<?> doc, Function<TextParagraphElementBase, Collection<Node>> nodesProvider
            , boolean skipParagraphChildNodes) {
        this.nodesProvider = nodesProvider;
        walker = new ParagraphWalker(doc, skipParagraphChildNodes);
        next = findNext();

    }

    private Node findNext() {
        return collectNodes().isEmpty() ? null : collectedNodes.remove(0);
    }

    private List<Node> collectNodes() {
        while (collectedNodes.isEmpty() && walker.hasNext()) {
            TextParagraphElementBase paragraph = walker.next();
            if (paragraph != null) {
                collectedNodes.addAll(nodesProvider.apply(paragraph));
            }
        }
        return collectedNodes;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Node next() {
        Node result = next;
        next = findNext();
        return result;
    }
}