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

package de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image;

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.odf.OdfNodeType;
import de.underdocx.tools.odf.ParagraphByParagraphNodesEnumerator;
import de.underdocx.tools.tree.Enumerator;
import de.underdocx.tools.tree.Nodes;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDollarImagePlaceholdersProvider implements EncapsulatedNodesExtractor, PlaceholdersProvider<OdtContainer, SimpleDollarImagePlaceholderData, OdfTextDocument> {
    private final OdfContainer<?> doc;

    public SimpleDollarImagePlaceholdersProvider(OdfContainer<?> doc) {
        this.doc = doc;
    }

    private static TextNodeInterpreter interpreter = OdfTextNodeInterpreter.INSTANCE;

    private Optional<DrawFrameElement> getFrame(Node node) {
        return Optional.ofNullable(node instanceof DrawFrameElement ? (DrawFrameElement) node : null);
    }

    private Optional<DrawImageElement> getImage(DrawFrameElement node) {
        Optional<Node> child = Nodes.findFirstDescendantNode(node, OdfNodeType.IMAGE_ELEMENT.getElementName());
        return child.map(c -> (DrawImageElement) c);
    }

    private Optional<Pair<DrawFrameElement, DrawImageElement>> getImageElements(Node node) {
        return Optional.ofNullable(Convenience.build(pairWrapper -> {
            getFrame(node).ifPresent(frame -> {
                getImage(frame).ifPresent(image -> {
                    pairWrapper.value = new Pair<>(frame, image);
                });
            });
        }));
    }

    private Optional<SimpleDollarImagePlaceholderData> getImageDate(Node node) {
        return Optional.ofNullable(Convenience.build(w ->
                getImageElements(node).ifPresent(elements -> w.value = new SimpleDollarImagePlaceholderData(doc, elements))
        ));
    }

    @Override
    public List<Node> extractNodes(Node tree) {
        return Convenience.also(new ArrayList<Node>(), result -> {
            Nodes.findDescendantNodes(tree, OdfNodeType.FRAME_ELEMENT.getElementName()).forEach(frame -> {
                if (isEncapsulatedNode(frame)) result.add(frame);
            });
        });
    }

    @Override
    public boolean isEncapsulatedNode(Node node) {
        return Convenience.build(false, w -> {
            getImageDate(node).ifPresent(imageData -> w.value = SimpleDollarPlaceholdersProvider.regex.matches(imageData.getName()));
        });
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return interpreter;
    }

    @Override
    public Enumerator<Node> getPlaceholders() {
        return new ParagraphByParagraphNodesEnumerator(doc, p -> this.extractNodes(p), true);
    }

    @Override
    public SimpleDollarImagePlaceholderData getPlaceholderData(Node node) {
        return new SimpleDollarImagePlaceholderData(doc, getImageElements(node).get());
    }

    @Override
    public Optional<TextualPlaceholderToolkit<SimpleDollarImagePlaceholderData>> getPlaceholderToolkit() {
        return Optional.empty();
    }


}
