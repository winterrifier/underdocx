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

package de.underdocx.common.placeholder;

import de.underdocx.common.codec.Codec;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.tree.Nodes;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.underdocx.tools.common.Convenience.filter;
import static de.underdocx.tools.common.Convenience.first;

public class TextualPlaceholderToolkit<P> {
    private final EncapsulatedNodesExtractor extractor;
    private final Codec<P> codec;

    public TextualPlaceholderToolkit(EncapsulatedNodesExtractor extractor, Codec<P> codec) {
        this.codec = codec;
        this.extractor = extractor;
    }

    public List<Node> extractPlaceholders(Node tree) {
        return extractor.extractNodes(tree);
    }

    public void replacePlaceholderWithText(Node placeholder, String text) {
        extractor.getTextNodeInterpreter().setNodeText(placeholder, text);
    }

    private String getText(Node placeholder) {
        return placeholder.getFirstChild().getNodeValue();
    }

    public P parsePlaceholder(Node placeholder) {
        return UnderdocxExecutionException.expect(codec.parse(getText(placeholder)));
    }

    public void setPlaceholder(Node placeholder, P data) {
        replacePlaceholderWithText(placeholder, codec.getTextContent(data));
    }

    public static void deletePlaceholder(Node placeholder) {
        Node nextSibling = placeholder.getNextSibling();
        Node prevSibling = placeholder.getPreviousSibling();
        Node parent = placeholder.getParentNode();

        if (nextSibling != null && prevSibling != null && Nodes.isText(nextSibling, prevSibling)) {
            prevSibling.setNodeValue(prevSibling.getNodeValue() + nextSibling.getNodeValue());
            parent.removeChild(nextSibling);
        }
        parent.removeChild(placeholder);
    }

    public static Node clonePlaceholder(Node placeholder, boolean insertBefore) {
        return Nodes.cloneNode(placeholder, placeholder, insertBefore, true);
    }

    public List<Node> findPlaceholders(Node tree, Predicate<P> filter) {
        return filter(extractPlaceholders(tree), node -> filter.test(parsePlaceholder(node)));
    }

    public Optional<Node> findFirstPlaceholder(Node tree, Predicate<P> filter) {
        List<Node> list = findPlaceholders(tree, filter);
        return list.isEmpty() ? Optional.empty() : Optional.of(first(list));
    }
}
