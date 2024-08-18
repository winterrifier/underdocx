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

package de.underdocx.common.placeholder.image;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
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

public class ImagePlaceholders implements EncapsulatedNodesExtractor, PlaceholdersProvider<OdtContainer, ImageData, OdfTextDocument> {
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
        return getImageElements(node).isPresent();
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return interpreter;
    }

    @Override
    public Enumerator<Node> getPlaceholders(OdtContainer doc) {
        return new ParagraphByParagraphNodesEnumerator(doc, p -> this.extractNodes(p), true);
    }

    @Override
    public ImageData getPlaceholderData(Node node) {
        return null;
    }

    @Override
    public Optional<TextualPlaceholderToolkit<ImageData>> getPlaceholderToolkit() {
        return Optional.empty();
    }
}
