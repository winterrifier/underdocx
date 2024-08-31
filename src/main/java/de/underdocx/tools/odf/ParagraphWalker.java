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
import de.underdocx.tools.tree.TreeWalker;
import de.underdocx.tools.tree.enumerator.Enumerator;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.w3c.dom.Node;

import java.util.function.Predicate;

import static de.underdocx.tools.common.Convenience.*;

public class ParagraphWalker implements Enumerator<TextParagraphElementBase> {

    private final TreeWalker treeWalker;
    private final boolean skipChildren;
    private TextParagraphElementBase next;

    public ParagraphWalker(OdfContainer<?> doc, boolean skipParagraphChildNodes) {
        Node node = doc.getContentDom();
        this.treeWalker = new TreeWalker(node, node);
        this.skipChildren = skipParagraphChildNodes;
        findNext();
    }

    private void findNext() {
        if (next != null && skipChildren) {
            treeWalker.nextSkipChildren();
            next = null;
        }
        Predicate<TreeWalker.VisitState> p = state -> state != null && state.isBeginVisit() && state.getNode() instanceof TextParagraphElementBase;
        next = (TextParagraphElementBase) getOrDefault(treeWalker.next(p), TreeWalker.VisitState::getNode, null);
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public TextParagraphElementBase next() {
        return also(next, n -> findNext());
    }
}
