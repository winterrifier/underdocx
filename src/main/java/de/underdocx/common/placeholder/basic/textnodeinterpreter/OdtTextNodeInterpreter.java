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

package de.underdocx.common.placeholder.basic.textnodeinterpreter;

import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.odf.OdfTools;
import de.underdocx.tools.tree.Nodes;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static de.underdocx.tools.common.Convenience.also;

public class OdtTextNodeInterpreter extends AbstractOdtTextNodeInterpreter {


    @Override
    public void setNodeText(Node node, String text) {
        Nodes.deleteChildren(node);
        OdfWhitespaceProcessor.appendText((Element) node, text);
    }

    @Override
    public Node createTextContainer(Node parent) {
        // return parent.getOwnerDocument().createElementNS("urn:oasis:names:tc:opendocument:xmlns:text:1.0", spanElementName);
        try {
            return also(new TextSpanElement(OdfTools.getFileDom(parent)), span -> parent.appendChild(span));
        } catch (Exception e) {
            throw new UnderdocxExecutionException(e);
        }

    }

}
