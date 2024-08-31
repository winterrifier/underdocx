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

package de.underdocx.enginelayers.baseengine.modifiers.appendplaceholder;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.modifiers.Modifier;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Convenience;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;

public class AppendPlaceholderModifier<P> implements Modifier<OdtContainer, P, OdfTextDocument, P> {
    @Override
    public boolean modify(Selection<OdtContainer, P, OdfTextDocument> selection, P modifierData) {
        return Convenience.build(false, result -> {
            selection.getPlaceholderToolkit().ifPresent(toolkit -> {
                Node clone = TextualPlaceholderToolkit.clonePlaceholder(selection.getNode(), true);
                toolkit.setPlaceholder(clone, modifierData);
                try {
                    OdfTextParagraph newParagraph = selection.getDocContainer().getDocument().newParagraph();
                    newParagraph.appendChild(clone);
                    result.value = true;
                } catch (Exception e) {
                    throw new UnderdocxExecutionException(e);
                }
            });
        });
    }
}
