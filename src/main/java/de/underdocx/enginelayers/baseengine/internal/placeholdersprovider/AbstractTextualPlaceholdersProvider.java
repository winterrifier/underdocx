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

package de.underdocx.enginelayers.baseengine.internal.placeholdersprovider;

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.tools.odf.ParagraphWalker;
import de.underdocx.tools.tree.Enumerator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractTextualPlaceholdersProvider<C extends OdfContainer<D>, P, D extends OdfDocument> implements PlaceholdersProvider<C, P, D> {

    private final TextualPlaceholderToolkit<P> toolkit;

    protected AbstractTextualPlaceholdersProvider(TextualPlaceholderToolkit<P> toolkit) {
        this.toolkit = toolkit;
    }

    @Override
    public Enumerator<Node> getPlaceholders(C doc) {
        return new NodesEnumerator(doc);
    }

    private class NodesEnumerator implements Enumerator<Node> {
        private ParagraphWalker walker;
        private List<Node> collectedNodes = new ArrayList<>();
        private Node next = null;

        public NodesEnumerator(C doc) {
            walker = new ParagraphWalker(doc, true);
            next = findNext();
        }

        private Node findNext() {
            return collectNodes().isEmpty() ? null : collectedNodes.remove(0);
        }

        private List<Node> collectNodes() {
            while (collectedNodes.isEmpty() && walker.hasNext()) {
                TextParagraphElementBase paragraph = walker.next();
                if (paragraph != null) {
                    collectedNodes.addAll(toolkit.extractPlaceholders(paragraph));
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

    @Override
    public P getPlaceholderData(Node node) {
        return toolkit.parsePlaceholder(node);
    }

    @Override
    public Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit() {
        return Optional.of(toolkit);
    }

}
