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

package de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.modifiers.Modifier;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Wrapper;
import de.underdocx.tools.odf.OdfTools;
import de.underdocx.tools.tree.nodepath.TextNodePath;
import de.underdocx.tools.tree.nodepath.TreeNodeCollector;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class DeletePlaceholderModifier<C extends DocContainer<D>, P, D> implements Modifier<C, P, D, DeletePlaceholderModifierData> {
    @Override
    public boolean modify(Selection<C, P, D> selection, DeletePlaceholderModifierData modifierData) {
        return modify(selection.getNode(), modifierData);
    }

    public static boolean modify(Node placeholderNode, DeletePlaceholderModifierData modifierData) {
        return Convenience.build(false, result -> {
            OdfTools.findAscendantParagraph(placeholderNode, false).ifPresent(p -> {
                // TODO copy pageBreak info to next paragraph if requested
                TextualPlaceholderToolkit.deletePlaceholder(placeholderNode);
                if (modifierData.getDeleteStrategy() != DeletePlaceholderModifierData.Strategy.KEEP_PARAGRAPH) {
                    if (modifierData.getDeleteStrategy() == DeletePlaceholderModifierData.Strategy.DELETE_PARAGRAPH) {
                        removeParagraph(result, p);
                    } else {
                        List<Node> paragraphNodes = new TreeNodeCollector(p, p, new ArrayList<>()).collect();
                        TextNodePath path = new TextNodePath(paragraphNodes, OdfTextNodeInterpreter.INSTANCE);
                        if (path.isTextOnly()) {
                            String textContent = path.fetchTextContent();
                            if (textContent.isEmpty() || (textContent.isBlank() && modifierData.getDeleteStrategy() == DeletePlaceholderModifierData.Strategy.DELETE_BLANK_PARAGRAPH)) {
                                removeParagraph(result, p);
                            }
                        }
                    }
                }
            });
        });
    }

    private static void removeParagraph(Wrapper<Boolean> result, Node p) {
        p.getParentNode().removeChild(p);
        result.value = true;
    }


}
